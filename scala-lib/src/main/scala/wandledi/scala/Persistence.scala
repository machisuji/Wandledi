package wandledi.scala

import wandledi.java.Database
import collection.JavaConversions.asIterable

trait Persistence {

  def getDatabase: Database

  protected lazy implicit val implicitDb: Database = getDatabase
}

trait Entity {
  
  def save(implicit db: Database) = db.persist(this)
  def merge(implicit db: Database) = db.merge(this)
  def refresh(implicit db: Database) = db.refresh(this)
  def delete(implicit db: Database) = db.remove(this)
}

trait EntityCompanion[T] {

  /**Implement this and everything will be alright.*/
  def entityClass: Class[T]

  def findAll(implicit db: Database): Iterable[T] = db.findAll[T](entityClass)
  def query(query: String, args: Object*)(implicit db: Database): Iterable[T] =
    db.query[T](entityClass, query, args: _*)
  def query(query: String, start: Int, rows: Int, args: Object*)(implicit db: Database): Iterable[T] =
    db.query[T](entityClass, query, start, rows, args: _*)
  def querySingle(query: String, args: Object*)(implicit db: Database) =
    db.querySingle[T](entityClass, query, args: _*)
  def delete(key: Object)(implicit db: Database) = db.remove[T](entityClass, key)
}
