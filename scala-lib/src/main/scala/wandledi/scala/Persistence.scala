package wandledi.scala

import wandledi.java.Database
import collection.JavaConversions._

trait Persistence {

  def database: Database

  protected lazy implicit val implicitDb: Database = database
}

trait Entity {
  // @TODO define options (such as commit, transaction, etc.)
  def save(options: Any*)(implicit db: Database) = db.persist(this)
  def merge(options: Any*)(implicit db: Database) = db.merge(this)
  def refresh(options: Any*)(implicit db: Database) = db.refresh(this)
  def delete(options: Any*)(implicit db: Database) = db.remove(this)
}

trait EntityCompanion[T] {

  /**Implement this and everything will be all right.*/
  def entityClass: Class[T]

  def findAll(implicit db: Database): Iterable[T] = db.findAll[T](entityClass)
  def find(pk: Any)(implicit db: Database): T = db.find[T](entityClass, pk)
  def query(query: String, args: Object*)(implicit db: Database): Iterable[T] =
    db.query[T](entityClass, query, args: _*)
  def query(query: String, start: Int, rows: Int, args: Object*)(implicit db: Database): Iterable[T] =
    db.query[T](entityClass, query, start, rows, args: _*)
  def querySingle(query: String, args: Object*)(implicit db: Database) =
    db.querySingle[T](entityClass, query, args: _*)
  def delete(key: Object)(implicit db: Database) = db.remove[T](entityClass, key)
}
