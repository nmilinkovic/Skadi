package skadi.container.processing

import scala.collection.mutable
import scala.collection.immutable.Queue

import skadi.beans.Bean

/**
 * Performs a topological sort of the given beans by creating a dependency graph
 * using bean names as node identifier within the graph.
 * This allows beans to be instantiated in proper order (i.e. first beans
 * without dependencies are instantiated, then the beans depending on those and
 * then beans depending on a those etc.).
 *
 * @author Nikola Milinkovic
 */
private[container] class TopologicalBeanSorter extends BeanProcessor {

  /**
   * Performs a topological sort on the dependency graph of given beans. Graph
   * is created by using the bean name as the node identifier and bean names in
   * bean arguments as edges.
   *
   * @param beans beans to be sorted
   *
   * @return a sequence of beans sorted in topological order
   */
  override def process(beans: Seq[Bean]): Seq[Bean] = {

    log.info("Sorting the beans...")

    val namesMap = Map.empty ++ beans.map(b => (b.name, b))
    val graph = constructGraph(namesMap)
    val sortedNames = topSort[Symbol](graph)
    for {
      name <- sortedNames
      bean = namesMap.get(name).get
    } yield bean
  }

  private def constructGraph(namesMap: Map[Symbol, Bean]): Set[(Symbol, Set[Symbol])] = {
    val nodes = for {
      name <- namesMap.keys
      names = namesMap.keySet.toList
      bean = namesMap.getOrElse(name, null)
      constructorDependencies = extractNames(bean.args, names)
      setterDependencies = extractNames(bean.injectables.map(_._2), names)
      dependencies = constructorDependencies ++ setterDependencies
    } yield (name, dependencies)
    Set.empty ++ nodes
  }

  private def extractNames(args: List[Any], allNames: List[Symbol]): Set[Symbol] = {
    // names of other beans in the argument list represent  beans that this
    // bean depends on
    val extractedNames = new mutable.ListBuffer[Symbol]
    for (arg <- args) arg match {
      case name: Symbol if (allNames.contains(name)) => extractedNames += name
      case _ => //not a reference, ignore it
    }
    val result = mutable.Set.empty[Symbol]
    for (eachName <- extractedNames) {
      result += eachName
    }

    Set.empty ++ result
  }

  /**
   * Performs a topological sort on a set of nodes. Nodes are defined as a set
   * of tuples where the first element of the tuple is the node itself and the
   * second element of the tuple are the edges of the node.
   *
   * @param nodes
   *             a set of nodes to be sorted
   * @return a sequence of topologically sorted nodes
   *
   * @throws IllegalArgumentException if the passed nodes are null or if the
   * passed graph is not a directed acyclic graph (DAG)
   */
  private def topSort[T](nodes: Set[(T, Set[T])]): Seq[T] = {

    require(nodes != null, "Graph nodes passed as null!")

    // Finds all nodes that have no more dependencies
    def findEnds(nodes: Set[(T, Set[T])]): Set[(T, Set[T])] = {
      for {
        node <- nodes
        if (node._2.isEmpty)
      }  yield node
    }

    // extracts the names from the given nodes
    def extractNames(nodes: Set[(T, Set[T])]): Seq[T] = {
      nodes.map(_._1).toSeq
    }

    // Removes the ends from the graph, also remove them from the dependencies
    // of the remaining nodes. Returns a set of remaining nodes.
    def removeEnds(ends: Set[(T, Set[T])], nodes: Set[(T, Set[T])]): Set[(T, Set[T])] = {
      val prunedNodes = for {
        node <- nodes
        if (!ends.contains(node))
      } yield (node._1, node._2 -- extractNames(ends))
      Set.empty ++ prunedNodes
    }

    // main algorithm
    if (nodes.size <= 1) extractNames(nodes)
    else {
     val ends = findEnds(nodes)
     require(!ends.isEmpty, "Cyclic dependency detected in nodes: " + nodes)
     val prunedNodes = removeEnds(ends, nodes)
     extractNames(ends) ++ topSort(prunedNodes)
    }
  }

}
