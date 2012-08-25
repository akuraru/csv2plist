import java.io.PrintWriter
import java.io.FileReader
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileInputStream
import java.io.File
import au.com.bytecode.opencsv.CSVReader
import scala.io.Source

object csv2plist {
  def main(args: Array[String]): Unit = {
    val (filename, hasTitleRow, hasArray) = ArgumentCheck(args);
    if (filename == "") {
      println("Usage: scala csv2plist.scala [-t] <filename>\n");
      println("  -t             The file has a title row.\n");
      return ;
    }

    val file = new File(filename);
    if (file.exists == false) {
      println("No such file " + filename)
      return ;
    }
    val reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "utf-8"))
    val body = createBody(reader, hasTitleRow, hasArray)

    val outputFilename = filename.replace(".csv", ".plist");
    val fp_out = new PrintWriter(outputFilename, "utf-8");

    fp_out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
      "<plist version=\"1.0\">\n" +
      "<array>\n" +
      body +
      "</array>\n" +
      "</plist>");
    fp_out.close();

    println("Generated")
  }
  def createBody(reader: CSVReader, hasTitleRow: Boolean, hasArray: Boolean): String = {
    if (hasArray == false) {
      val keys = if (hasTitleRow) reader.readNext else Array[String]();

      Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).map {
        values =>
          def g(i: Int): String = {
            if (hasTitleRow) "\t\t<key>" + keys(i) + "</key>\n"
            else "\t\t<key>keyname" + i + "</key>\n"
          }
          def f(l: List[String], i: Int): String = {
            l match {
              case h :: t => g(i) + "\t\t<string>" + h + "</string>\n" + f(t, i + 1)
              case Nil => ""
            }
          };
          "\t<dict>\n" + f(values, 0) + "\t</dict>\n"
      }.fold("")(_ + _)
    } else {
      val keys = reader.readNext;
      val values = Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).toList
      def f(l : List[List[String]], i : Int):String = {
        l match {
          case h :: t => {
            h match {
              case h2 :: t2 :: _ => (if(i < h2.toInt)"\t\t<string>" + t2 + "</string>\n" else "\t</array>\n" + "\t<array>\n" + "\t\t<string>" + t2 + "</string>\n") + f(t, h2.toInt)  
              case _ => "" + f(t, i)
            }
          }
          case Nil => "\t</array>\n"
        }
      }
      "\t<array>\n" + f(values, -1)
    }
  }

  def ArgumentCheck(args: Array[String]): (String, Boolean, Boolean) = {
    if (args.length == 1) {
      return (args(0), false, false);
    } else if (args.length == 2) {
      return (args(1), args(0) == "-t", args(0) == "-a");
    } else if (args.length == 3) {
      val t = (args(0) == "-t" || args(1) == "-t") && (args(0) == "-a" || args(1) == "-a")
      return (if (t) args(2) else "", t, t);
    } else {
      ("", false, false);
    }
  }
}