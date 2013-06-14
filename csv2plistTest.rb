$count = 0
$success = 0
$testFile = "sample/root.csv"
$/
require './csv2plist.rb'

def test (obj , result)
    if obj != result then
        p obj
        p " is not "
        p result
        elsif
        $success = $success + 1
    end
    $count = $count + 1
end
def endTest ()
    puts ""
    puts "#{$success} / #{$count} Test"
end

user = CsvToPlist.new
str = user.fileRead($testFile)
test(str, [["id", "テスト","本文"],
['1',"テスト1",'改行
含み'],
["2","test2","カンマ,含み"],
["4","てすと3","ダブルクォーテーション\"含み"],
["1","っってすと","てふてふ"],
["2","Test","特殊文字&lt;&gt;&amp;\\|"],
["2","空",""],
])

test(str[1..-1], [
     ['1',"テスト1","改行\n含み"],
     ["2","test2","カンマ,含み"],
     ["4","てすと3","ダブルクォーテーション\"含み"],
     ["1","っってすと","てふてふ"],
     ["2",'Test','特殊文字&lt;&gt;&amp;\\|'],
     ["2","空",""],
     ])

test(str[0], ["id", "テスト","本文"])

init = NSArray.new(0, str[1..-1], str[0], 0)
test(init, NSArray.new(0, [
 NSDictionary.new(1, [[NSString.new(2, "1"), NSKey.new(2, "id")],[NSString.new(2, "テスト1"), NSKey.new(2, "テスト")],[NSString.new(2, "改行\n含み"), NSKey.new(2, "本文")]]),
NSDictionary.new(1, ["2","test2","カンマ,含み"], ["id", "テスト","本文"]),
NSDictionary.new(1, ["4","てすと3","ダブルクォーテーション\"含み"], ["id", "テスト","本文"]),
NSDictionary.new(1, ["1","っってすと","てふてふ"], ["id", "テスト","本文"]),
NSDictionary.new(1, ["2",'Test','特殊文字&lt;&gt;&amp;\\|'], ["id", "テスト","本文"]),
NSDictionary.new(1, ["2","空",""], ["id", "テスト","本文"]),
]))
dictStr = NSDictionary.new(0, [[NSString.new(1, "1"), NSKey.new(1, "id")],[NSString.new(1, "テスト1"), NSKey.new(1, "テスト")],[NSString.new(1, "改行\n含み"), NSKey.new(1, "本文")]]).to_s
test(dictStr, '<dict>
    <key>id</key>
    <string>1</string>
    <key>テスト</key>
    <string>テスト1</string>
    <key>本文</key>
    <string>改行
含み</string>
</dict>
')
     
arrayStr = NSArray.new(0, [NSString.new(1, "2"), NSString.new(1, "test2"), NSString.new(1, "カンマ,含み")]).to_s
test(arrayStr, '<array>
    <string>2</string>
    <string>test2</string>
    <string>カンマ,含み</string>
</array>
')
     
hierarch1 = NSArray.new(0, str[1..-1], str[0], 1)
test(hierarch1, NSArray.new(0, [
    NSArray.new(1, [
NSDictionary.new(2, [[NSString.new(3, "テスト1"), NSKey.new(3, "テスト")],[NSString.new(3, "改行\n含み"), NSKey.new(3, "本文")]]),
NSDictionary.new(2, ["test2","カンマ,含み"], ["テスト","本文"]),
NSDictionary.new(2, ["てすと3","ダブルクォーテーション\"含み"], ["テスト","本文"]),
  ]),
  NSArray.new(1, [
    NSDictionary.new(2, ["っってすと","てふてふ"], ["テスト","本文"]),
    NSDictionary.new(2, ['Test','特殊文字&lt;&gt;&amp;\|'], ["テスト","本文"]),
  ]),
  NSArray.new(1, [
    NSDictionary.new(2, ["空",""], ["テスト","本文"]),
  ]),
]))


     
endTest()