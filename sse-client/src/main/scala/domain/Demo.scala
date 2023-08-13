package domain

import java.util.Locale
import scala.util.Random

case class Demo(id: Int, name: String)

object Demo{
	def randomDemo: Demo = Demo(
		Random.between(999, 9999),
		Random.nextString(8).formatLocal(Locale.ENGLISH)
	)
	
	def randomDemoSeq(n: Int): Seq[Demo] = Seq.fill(n)(randomDemo)
}
