package skadi.container.validation

import skadi.beans.Bean

private[container] trait Validator {

  def validate(beans: Seq[Bean]): List[Error]

}
