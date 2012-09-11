import org.junit.Assert._;
import org.junit.Test;
import java.io.InputStreamReader
import java.io.FileInputStream
import java.io.File
import au.com.bytecode.opencsv.CSVReader

class csv2plistTest {
  val listLen = 1000
  val pCenter = 1
  
  @Test
  def test() {
    val a = csv2plist.stringWithTuple(("",""))
    assert(a == "\t\t<key></key>\n\t\t<string></string>\n")
  }
  @Test
  def testSWT2() {
    val a = csv2plist.stringWithTuple(("key","body"))
    assert(a == "\t\t<key>key</key>\n\t\t<string>body</string>\n")
  }
  @Test
  def testSWM1() {
    val a = csv2plist.stringWithTList(List(("","")))
    assert(a == "\t<dict>\n\t\t<key></key>\n\t\t<string></string>\n\t</dict>\n")
  }
  @Test
  def testSWM2() {
    val a = csv2plist.stringWithTList(List(("",""),("","")))
    assert(a == "\t<dict>\n\t\t<key></key>\n\t\t<string></string>\n\t\t<key></key>\n\t\t<string></string>\n\t</dict>\n")
  }
  @Test
  def testSWM3() {
    val a = csv2plist.stringWithTList(List(("key1","body1"),("key2","body2")))
    assert(a == "\t<dict>\n\t\t<key>key1</key>\n\t\t<string>body1</string>\n\t\t<key>key2</key>\n\t\t<string>body2</string>\n\t</dict>\n")
  }
  @Test
  def testSWSList1() {
    val a = csv2plist.stringWithSList(List(""))
    assert(a == "\t<array>\n\t\t<string></string>\n\t</array>\n")
  }
  @Test
  def testSWSList2() {
    val a = csv2plist.stringWithSList(List("",""))
    assert(a == "\t<array>\n\t\t<string></string>\n\t\t<string></string>\n\t</array>\n")
  }
  @Test
  def testSWSLList1() {
    val a = csv2plist.stringWithSLList(List(List("")))
    assert(a == "<array>\n\t<array>\n\t\t<string></string>\n\t</array>\n</array>\n")
  }
  @Test
  def testSWSLList2() {
    val a = csv2plist.stringWithSLList(List(List("","")))
    assert(a == "<array>\n\t<array>\n\t\t<string></string>\n\t\t<string></string>\n\t</array>\n</array>\n")
  }
  @Test
  def testSWSLList3() {
    val a = csv2plist.stringWithSLList(List(List(""),List("")))
    assert(a == "<array>\n\t<array>\n\t\t<string></string>\n\t</array>\n\t<array>\n\t\t<string></string>\n\t</array>\n</array>\n")
  }
  
  
  
  @Test
  def testCell1() {
    val array = List("")
    val a = csv2plist.cell(List(""),array)
    assert(a == List(("","")))
  }
  @Test
  def testCell2() {
    val array = List("key")
    val a = csv2plist.cell(List("value"),array)
    assert(a == List(("key","value")))
  }
  
  @Test
  def testOptTA1() {
    val filename = "contants2.csv"
    val reader =  new CSVReader(new InputStreamReader(new FileInputStream(new File(filename)), "utf-8"))
    val a = csv2plist.OptionTA(reader)
    val ans = List(List(List(("テスト","テスト1"),("本文","改行\n含み")),List(("テスト","test2"),("本文","カンマ,含み")),List(("テスト","てすと3"),("本文","ダブルクォーテーション\"含み"))),List(List(("テスト","っってすと"),("本文","てふてふ"))))
    assert(a == ans)
  }
  @Test
  def testCreateTA() {
    val filename = "contants2.csv"
    val reader =  new CSVReader(new InputStreamReader(new FileInputStream(new File(filename)), "utf-8"))
    val a = csv2plist.createBody(reader, true, true)
    val ans = "<array>\n<array>\n\t<dict>\n\t\t<key>テスト</key>\n\t\t<string>テスト1</string>\n\t\t<key>本文</key>\n\t\t<string>改行\n含み</string>\n\t</dict>\n\t<dict>\n\t\t<key>テスト</key>\n\t\t<string>test2</string>\n\t\t<key>本文</key>\n\t\t<string>カンマ,含み</string>\n\t</dict>\n\t<dict>\n\t\t<key>テスト</key>\n\t\t<string>てすと3</string>\n\t\t<key>本文</key>\n\t\t<string>ダブルクォーテーション\"含み</string>\n\t</dict>\n</array>\n<array>\n\t<dict>\n\t\t<key>テスト</key>\n\t\t<string>っってすと</string>\n\t\t<key>本文</key>\n\t\t<string>てふてふ</string>\n\t</dict>\n</array>\n</array>\n"
    assert(a == ans)
  }
  @Test
  def testOptionA() {
    val filename = "contants2.csv"
    val reader =  new CSVReader(new InputStreamReader(new FileInputStream(new File(filename)), "utf-8"))
    val a = csv2plist.OptionA(reader)
    val ans = List(List("テスト1","test2","てすと3"),List("っってすと"))
    assert(a == ans)
  }
  @Test
  def testCreateA() {
    val filename = "contants2.csv"
    val reader =  new CSVReader(new InputStreamReader(new FileInputStream(new File(filename)), "utf-8"))
    val a = csv2plist.createBody(reader, false, true)
    val ans = "<array>\n\t<array>\n\t\t<string>テスト1</string>\n\t\t<string>test2</string>\n\t\t<string>てすと3</string>\n\t</array>\n\t<array>\n\t\t<string>っってすと</string>\n\t</array>\n</array>\n"
    assert(a == ans)
  }
}