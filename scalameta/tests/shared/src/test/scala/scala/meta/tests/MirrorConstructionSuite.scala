package scala.meta
package tests

import org.scalatest._
import scala.meta.internal.semantic.{vfs => v}
import scala.meta.internal.semantic.{schema => s}

trait BaseSemanticSuite extends FunSuiteLike {
  val sourcepath = Sourcepath(BuildInfo.mirrorSourcepath)
  val classpath = Classpath(BuildInfo.mirrorClasspath)
}

class MirrorConstructionSuite extends BaseSemanticSuite {
  def semanticdbs: List[AbsolutePath] = classpath.deep.collect {
    case path if v.SemanticdbPaths.isSemanticdb(path.name) =>
      path.base.resolve(path.name)
  }

  test("Database.load(Classpath, Sourcepath)") {
    val mirror = Database.load(classpath, sourcepath)
    assert(mirror.sources.nonEmpty)
  }

  test("Database.load(Array[Byte])") {
    semanticdbs.foreach { path =>
      val mirror = Database.load(path.readAllBytes)
      assert(mirror.sources.nonEmpty, path.toString)
    }
  }

  test("Database.load(Classpath)") {
    val mirror = Database.load(classpath)
    assert(mirror.sources.nonEmpty)
  }

  test("s.Attributes.filename has no Windows slash (\\)") {
    semanticdbs.foreach { path =>
      val sattrs = s.Attributes.parseFrom(path.readAllBytes)
      assert(!sattrs.filename.contains('\\'))
    }
  }
}
