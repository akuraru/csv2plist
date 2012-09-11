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
      body +
      "</plist>");
    fp_out.close();

    println("Generated")
  }
  def createBody(reader: CSVReader, hasTitleRow: Boolean, hasArray: Boolean): String = {
    if (hasArray == false) {
      val first = reader.readNext;
      val keys = if (hasTitleRow) first else { (0 to first.length - 1).map(x => "keyname" + x) }.toArray;
      val firstCell = if (hasTitleRow) "" else createCell(first.toList, keys)

      "<array>\n" + firstCell + Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).map {
        createCell(_, keys)
      }.fold("")(_ + _) + "</array>\n"
    } else if(hasTitleRow){
      val TupleListList = OptionTA(reader)
      stringWithTLLList(TupleListList)
    }
    else{
      val TupleListList = OptionA(reader)
      stringWithSLList(TupleListList)
    }
  }

  def createCell(values: List[String], keys: Array[String]) = {
    "\t<dict>\n" + Cell(values, 0, keys) + "\t</dict>\n"
  }
  def Cell(l: List[String], i: Int, keys: Array[String]): String = {
    l match {
      case h :: t => "\t\t<key>" + keys(i) + "</key>\n" + "\t\t<string>" + h + "</string>\n" + Cell(t, i + 1, keys)
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

  def OptionTA(reader: CSVReader):List[List[List[(String,String)]]] = {
    val keys = reader.readNext.toList.tail;
     val values = Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).toList
    def f(l: List[List[String]], i: Int, m :List[List[(String,String)]], r:List[List[List[(String,String)]]]):List[List[List[(String,String)]]] = {
        l match {
          case h :: t => {
            h match {
              case h2 :: t2 =>
                if (i < h2.toInt)
                  f(t, h2.toInt, cell(t2,keys)::m , r)
                else
                  f(t, h2.toInt, cell(t2,keys)::Nil, m.reverse :: r)
              case _ => f(t, i, m, r)
            }
          }
          case Nil => (m.reverse :: r).reverse
        }
      }
      f(values, -1, Nil, Nil)
  }
  def OptionA(reader: CSVReader):List[List[String]] = {
    reader.readNext
     val values = Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).toList
      def f(l: List[List[String]], i: Int, m :List[String], r:List[List[String]]):List[List[String]] = {
        l match {
          case h :: t => {
            h match {
              case h2 :: t2 :: _ =>
                if (i < h2.toInt)
                  f(t, h2.toInt, t2::m , r)
                else
                  f(t, h2.toInt, t2::Nil, m.reverse :: r)
              case _ => f(t, i, m, r)
            }
          }
          case Nil => (m.reverse :: r).reverse
        }
      }
      f(values, -1, Nil, Nil)
  }
  def cell(l: List[String], keys: List[String]): List[(String, String)] = {
    l match {
      case h :: t =>
        keys match {
          case k :: ks =>
            (k, h) :: cell(t, ks)
          case Nil => Nil
        }
      case Nil => Nil
    }
  };
  def stringWithSLList(l: List[List[String]]): String = {
    "<array>\n" +
      l.map(stringWithSList).fold("")((z, n) => z + n) +
      "</array>\n"
  }
  def stringWithTLLList(l: List[List[List[(String, String)]]]): String = {
    "<array>\n" +
      l.map(stringWithTLList).fold("")((z, n) => z + n) +
      "</array>\n"
  }
  def stringWithTLList(l: List[List[(String, String)]]): String = {
    "<array>\n" +
      l.map(stringWithTList).fold("")((z, n) => z + n) +
      "</array>\n"
  }
  def stringWithSList(l: List[String]): String = {
    "\t<array>\n" +
      l.map(stringWithBody).fold("")((z, n) => z + n) +
      "\t</array>\n"
  }
  def stringWithTList(l: List[(String, String)]): String = {
    "\t<dict>\n" +
      l.map(stringWithTuple).fold("")((z, n) => z + n) +
      "\t</dict>\n"
  }
  def stringWithBody(s: String): String = {
    "\t\t<string>" + s + "</string>\n"
  }
  def stringWithTuple(t: Tuple2[String, String]): String = {
    "\t\t<key>" + t._1 + "</key>\n" +
      "\t\t<string>" + t._2 + "</string>\n"
  }
}