import java.io.PrintWriter
import java.io.FileReader
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileInputStream
import java.io.File
import au.com.bytecode.opencsv.CSVReader
import scala.io.Source

object csv2plist {
  def main(args : Array[String]) : Unit = { 
    val (filename, hasTitleRow) = ArgumentCheck(args);
    if(filename == "") {
      println("no file")
      return;
    }
    
    var reader = new CSVReader(new InputStreamReader(new FileInputStream( new File( filename ) ), "utf-8" ))

    val outputFilename = filename.replace(".csv",".plist");
    val fp_out = new PrintWriter(outputFilename, "utf-8");
    
    fp_out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    fp_out.write("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n");
    fp_out.write("<plist version=\"1.0\">\n");
    fp_out.write("<array>\n");
    
    val keys = if(hasTitleRow)reader.readNext else Array[String]();
    Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).foreach {
      values =>
      def f(l:List[String] , i:Int):String = {
        l match{ 
        	case h :: t => (if(hasTitleRow)"\t\t<key>" + keys(i) + "</key>\n"else"\t\t<key>keyname"+i+"</key>\n") + "\t\t<string>"+h+"</string>\n" + f(t, i+1)
        	case Nil => ""
        }
      };
      fp_out.write("\t<dict>\n" + f(values, 0) + "\t</dict>\n");  
    }
    fp_out.write("</array>\n" + "</plist>");
    fp_out.close();
    
    println("Generated")
  }
  def ArgumentCheck(args : Array[String]): (String, Boolean) = {
    if(args.length == 1) {
      return (args(0), false);
    }else if(args.length == 2 && args(0) == "-t"){
      return (args(1), true);
    }else {
        println("Usage: scala csv2plist.scala [-t] <filename>\n");
        println("  -t             The file has a title row.\n");
      ("", false);
    }
  }
}