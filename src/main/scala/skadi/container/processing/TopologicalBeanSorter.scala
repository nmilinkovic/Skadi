package skadi.container.processing

import skadi.beans.Bean

/**
 * Performs a topological sort of the given beans by creating a dependency graph
 * using bean names as node identifiers within the graph.
 * This allows beans to be instantiated in proper order (i.e. first beans
 * without dependencies are instantiated, then the beans depending on those and
 * then beans depending on those etc.).
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

    require(beans != null, "Beans passed as null!")

    log.info("Sorting the beans...")

    val namesMap = Map(beans.map(b => (b.name, b)): _*)
    val graph = constructGraph(namesMap)
    val sortedNames = topSort[Symbol](graph)

    sortedNames.map(namesMap(_))
  }

  private def constructGraph(namesMap: Map[Symbol, Bean]): Set[(Symbol, Set[Symbol])] = {
    val nodes = for {
      name <- namesMap.keys
      names = namesMap.keySet.toList
      bean = namesMap(name)
      constructorDependencies = extractNames(bean.args, names)
      setterDependencies = extractNames(bean.injectables.map(_._2), names)
      dependencies = constructorDependencies ++ setterDependencies
    } yield (name, dependencies)
    Set.empty ++ nodes
  }

  private def extractNames(args: List[Any], allNames: List[Symbol]): Set[Symbol] = {
    // names of other beans in the argument list represent  beans that this
    // bean depends on
    def getName(arg: Any): Option[Symbol] = arg match {
      case name: Symbol if (allNames.contains(name)) => Some(name)
      case _ => None
    }

    val possibleNames = args.map(getName(_))
    val definedNames = possibleNames.filter(_.isDefined)
    val extractedNames = definedNames.map(_.get)

    Set(extractedNames.toArray: _*)
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

    // Finds all nodes that have no more dependencies
    def findEnds(nodes: Set[(T, Set[T])]): Set[(T, Set[T])] = nodes.filter(_._2.isEmpty)

    // extracts the names from the given nodes
    def extractNames(nodes: Set[(T, Set[T])]): Seq[T] = nodes.map(_._1).toSeq

    // Removes the ends from the graph, also remove them from the dependencies
    // of the remaining nodes. Returns a set of remaining nodes.
    def removeEnds(ends: Set[(T, Set[T])], nodes: Set[(T, Set[T])]): Set[(T, Set[T])] = {
      val filteredNodes = nodes.filter(!ends.contains(_))
      filteredNodes.map(n => (n._1, n._2 -- extractNames(ends)))
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
