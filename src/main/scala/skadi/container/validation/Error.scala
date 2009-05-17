package skadi.container.validation

private[skadi] class Error(message: String, severity: Severity.Value) {

  override def toString: String = severity + ": " + message

}
