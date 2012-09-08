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
      val first = reader.readNext;
      val keys = if (hasTitleRow) first else { (0 to first.length - 1).map(x => "keyname" + x) }.toArray;
      val firstCell = if (hasTitleRow) "" else createCell(first.toList, keys)

      firstCell + Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).map {
        createCell(_, keys)
      }.fold("")(_ + _)
    } else {
      val keys = reader.readNext.toArray;
      val values = Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).toList
      def f(l: List[List[String]], i: Int): String = {
        l match {
          case h :: t => {
            h match {
              case h2 :: t2 :: t3 =>
                (if (i < h2.toInt)"" else "\t</array>\n" + "\t<array>\n") +
                (if (hasTitleRow)
                  "\t<dict>\n" + cell(h.tail, 1, keys) + "\t</dict>\n" + f(t, h2.toInt);
                else
                  "\t\t<string>" + t2 + "</string>\n" + f(t, h2.toInt))
              case _ => "" + f(t, i)
            }
          }
          case Nil => ""
        }
      }
      "\t<array>\n" + f(values, -1) + "\t</array>\n"
    }
  }

  def createCell(values: List[String], keys: Array[String]) = {
    "\t<dict>\n" + cell(values, 0, keys) + "\t</dict>\n"
  }
  def cell(l: List[String], i: Int, keys: Array[String]): String = {
    l match {
      case h :: t => "\t\t<key>" + keys(i) + "</key>\n" + "\t\t<string>" + h + "</string>\n" + cell(t, i + 1, keys)
      case Nil => ""
    }
  };
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