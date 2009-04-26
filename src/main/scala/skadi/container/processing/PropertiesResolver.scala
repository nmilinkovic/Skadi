package skadi.container.processing

import java.util.Properties

import scala.collection.jcl.Conversions.convertSet

import skadi.beans.Bean
import skadi.beans.Prop
import skadi.beans.Property

/**
 * Attempts to replace all property placeholders with user defined or system and
 * environment properties.
 *
 * @author Nikola Milinkovic
 */
private[container] class PropertiesResolver extends BeanProcessor {

  private val delimiter = ";"
  private val xml = "xml"

  override def process(beans: Seq[Bean]): Seq[Bean] = {
    val handles = beans.filter(classOf[skadi.util.PropertiesHandle] == _.clazz)
    val userProperties = readUserProperties(handles)

    val systemProperties = new Properties
    val envVariables = System.getenv.entrySet
    val systemProps = System.getProperties.entrySet
    envVariables.foreach(e => systemProperties.put(e.getKey, e.getValue))
    systemProps.foreach(e => systemProperties.put(e.getKey, e.getValue))

    beans.map(resolveProperties(_, userProperties, systemProperties))
  }

  // replaces placeholders in constructor arguments and injectables
  private def resolveProperties(bean: Bean, userProps: Properties,
                                sysProps: Properties): Bean = {
    // TODO find out how to deal with non-string properties
    bean.args = bean.args.map(resolveProperty(_, userProps, sysProps))
    bean.injectables = bean.injectables.map(i => (resolveProperty(i._1, userProps, sysProps),
                                                  i._2))
    bean
  }

  private def resolveProperty(x: Any, userProps: Properties,
                              sysProps: Properties): Any = x match {
    case Property(placeholder) => getProperty(placeholder, userProps, sysProps)
    case Prop(placeholder) => getProperty(placeholder, userProps, sysProps)
    case _ => x
  }

  private def getProperty(placeholder: String, userProperties: Properties,
                                systemProperties: Properties): Any = {
    // first check in user defined properties
    val property = if (userProperties.containsKey(placeholder)) {
      userProperties.getProperty(placeholder)

      // if not there, try system properties
    } else {
      systemProperties.getProperty(placeholder)
    }
    require(property != null, "Property placeholder '" + placeholder + "' " +
              "could not be resolved!")
    property
  }

  // reads user defined properties
  private def readUserProperties(handles: Seq[Bean]): Properties = {

    val classloader = if (currentThread.getContextClassLoader != null) {
      currentThread.getContextClassLoader
    } else getClass.getClassLoader
    assume(classloader != null, "Could not retrieve classloader!")

    val userProperties = new Properties
    val declarations = getDeclarations(handles.toList.flatMap(_.injectables))
    val filenames = declarations.flatMap(_.split(delimiter, -1)).map(_.trim)

    filenames.foreach(loadProperties(_, userProperties, classloader))
    userProperties
  }

  private def getDeclarations(injectables: List[(Any, Symbol)]): List[String] = {
    val filesInjectables = injectables.takeWhile(_._2 == 'files)
    filesInjectables.map(_._1.asInstanceOf[String])
  }

  // attempts to read properties from either a regular or xml properties file
  private def loadProperties(filename: String, properties: Properties,
                             classloader: ClassLoader): Unit = {

    val stream = classloader.getResourceAsStream(filename)
    require(stream != null, "'" + filename + "' not found on the classpath!")

    val ext = filename.substring(filename.lastIndexOf(".") + 1, filename.length)
    if (xml.equalsIgnoreCase(ext)) {
      properties.loadFromXML(stream)
    } else {
      properties.load(stream)
    }
  }


}
