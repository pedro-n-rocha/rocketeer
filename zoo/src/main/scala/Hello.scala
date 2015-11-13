package default

import java.util.Properties
import java.net.InetSocketAddress
import scala.util.Try

import org.apache.zookeeper.server.{NIOServerCnxnFactory, ZooKeeperServer}

import java.net.{InetAddress, Socket}
import java.util.UUID

import com.google.common.io.Files
import org.apache.commons.lang3.SystemUtils

import java.io._


object Hello extends App {
 
    val shutdownDeletePaths = new scala.collection.mutable.HashSet[String]()
    
    val snapshotDir = createTempDir

    val logDir = createTempDir
  
    val server = new ZooKeeperServer(snapshotDir, logDir, 500)
    
    val factory = new NIOServerCnxnFactory()
    
    factory.configure(new InetSocketAddress("0.0.0.0", 2181), 16)
    
    factory.startup(server)
    
    def isRunning: Boolean = Try(server.isRunning) getOrElse false
    
    println(s"ZooKeeperServer isRunning: $isRunning")
    
    def shutdown(): Unit = {
      println(s"Shutting down ZK NIOServerCnxnFactory.")
      factory.shutdown()
      deleteRecursively(snapshotDir)
      deleteRecursively(logDir)
    }
    
   // Helpers ------------------------------------------------------------------
    
   def createTempDir: File = {
    val dir = mkdir(new File(Files.createTempDir(), "zoo-tmp-" + UUID.randomUUID.toString))
    registerShutdownDeleteDir(dir)

    Runtime.getRuntime.addShutdownHook(new Thread("delete zoo temp dir " + dir) {
      override def run() {
        if (! hasRootAsShutdownDeleteDir(dir)) deleteRecursively(dir)
      }
    })
    dir
  }
 
  /** Makes a new directory or throws an `IOException` if it cannot be made */
  def mkdir(dir: File): File = {
    if (!dir.mkdir()) throw new IOException(s"Could not create dir $dir")
    dir
  }
  
   def registerShutdownDeleteDir(file: File) {
    shutdownDeletePaths.synchronized {
      shutdownDeletePaths += file.getAbsolutePath
    }
  }
   
   def deleteRecursively(file: File) {
    if (file != null) {
      if (file.isDirectory && !isSymlink(file)) {
        for (child <- listFilesSafely(file))
          deleteRecursively(child)
      }
      if (!file.delete()) {
        if (file.exists())
          throw new IOException("Failed to delete: " + file.getAbsolutePath)
      }
    }
  }
   
   def hasRootAsShutdownDeleteDir(file: File): Boolean = {
    val absolutePath = file.getAbsolutePath
    shutdownDeletePaths.synchronized {
      shutdownDeletePaths.exists { path =>
        !absolutePath.equals(path) && absolutePath.startsWith(path)
      }
    }
  }
   
   def isSymlink(file: File): Boolean = {
    if (file == null) throw new NullPointerException("File must not be null")
    if (SystemUtils.IS_OS_WINDOWS) return false
    val fcd = if (file.getParent == null) file else new File(file.getParentFile.getCanonicalFile, file.getName)
    if (fcd.getCanonicalFile.equals(fcd.getAbsoluteFile)) false else true
  }

  def listFilesSafely(file: File): Seq[File] = {
    val files = file.listFiles()
    if (files == null) throw new IOException("Failed to list files for dir: " + file)
    files
  }
  
}