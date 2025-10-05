# 概要

`work-copilot`是基于`java`构建的命令行程序，别名为`j`，后文简称`wcp`，支持以**交互模式**或者**快捷模式**执行

快捷模式下：执行完单次命令后关闭

交互模式下：支持等待读取命令



基本格式如下

**快捷模式**：

```bash
j <command> <args>
```

**交互模式**：

```bash
j
# 然后进入交互，输入<command> <args>
copilot > ...
```



`wcp`本质是一个脚本注册器。你可以：

- 为应用程序注册别名，然后通过别名快速打开应用程序
- 配置日报，并且上传到`github`，从`github`拉取，或者查找日报中的某些内容
- 自定义脚本和扩展，构建你需要的工作流
- 在`cli`环境快捷搜索浏览器
- 为网页注册别名，通过别名快捷打开网页
- 使用计时器之类的小工具...



# 使用方法

## `alias` 相关

增加别名

```bash
j set <alias> <path>
```



移除别名

```bash
j remove <alias>
j rm <alias>
```



重命名别名

```bash
j rename <alias> <new_alias>
j rn <alias> <new_alias>
```



修改别名指向

```bash
j modify <alias> <new_path>
j mf <alias> <new_path>
```



## `category` 相关

`<category>` 可以为:

-  `browser` 可以打开网页，或者直接调用搜索引擎搜索内容
-  `editor` 可以打开路径
-  `script` 可执行的脚本
-  `vpn` vpn
- `outer_url` 外部url，需要开启vpn访问
- `inner_url`内部url



标记分类

```bash
j note <alias> <category>
j nt <alias> <categort>
```



解标记分类

```bash
j denote <alias> <category>
j dnt <alias> <categort>
```





## `report` 相关

使用日报，需要先在配置文件配置日报地址

默认为 `/Applications/work_copilot_java/report/`



写入日报

```bash
j report "<content>"
j r "<content>"
```



查看日报

```bash
j check [tail_line]
j c [tail_line]
# tail_line缺省为5
```



搜索日报

```bash
j search <key_word>
j sch <key_word>
j look <key_word>
j look <key_word> -f
# -f 选项表示忽略大小写
```





## `script` 相关

拼接脚本

```bash
j concat <script_name> <script_content>
```





## `find` 相关

查找别名

```bash
j find [path|browser|editor|editor|vpn|script] <alias>
j contain [path|browser|editor|editor|vpn|script] <alias>
# [path|browser|editor|editor|vpn|script]缺省为all
```



## `system` 相关

查看版本

```bash
j version
j v
```



启动计时器

```bash
j time countdown 10s
j time countdown 10m
j time countdown 1h
```



查看帮助信息

```bash
j help
j h
```



查看系统状态

```bash
j ps
```



切换日志模式

```bash
j log mode verbose
j log mode concise
```



查看列表

```bash
j list [all|script|path|browser|report|...]
j ls [all|script|path|browser|report|...]
```



退出

```bash
j exit
```



修改某些配置项，比如：修改默认的搜索引擎

```bash
j change setting search-engine [engine_name]
# engine_name可以为 bing, google, baidu
```





## `open` 相关

打开别名对应的路径

```bash
j <alias>
# alias可以为应用路径或者url,或者script_name
```



用编辑器打开某个文件

```bash
j <editor_alias> <dir_path>
```



用浏览器搜索网页

```bash
j bs <search_key_word>
```



# 进阶技巧

## 工具结构

`wcp`的目录结构如下和规范如下：

- `bin` 二进制文件、配置文件（`application.yaml`）存储
- `output` 输出目录，所有`wcp`输出的内容都在此
- `report` 日报文件、日报时间同步文件存储的位置
- `script` 脚本存储的位置，例如`concat`命令产生的脚本将会在这里
- `link` 软链接，一些`path`中带空格的，可以先在此目录创建一个`path`的软链接，然后给这个软链接设置别名打开
- `plugin` 插件仓库，可以将`wcp`需要使用的外部二进制程序放在这里集中管理



`wcp`的正常工作，依赖于`application.yaml`文件





# 使用案例

## 使用vscode打开某个目录

```bash
j
set code "vscode_path"
nt code editor
code "file_dir_path"
```





## 打开多个应用

```bash
j
set wx "weixin_path"
set code "vscode_path"
concat start_multi_app "j wx & j code"
start_multi_app
```





## 在命令行使用浏览器搜索内容

```bash
j
set bs "chrome_path"
nt bs browser
bs "我的小马名字叫珍珠"
```





## 自动化下载B站音频并转换为flac

`BBDown`是`github`上的一个下载b站音频的工具

`filetranslate`是一个用`ffmpeg`将`m4a`格式文件转为`flac`格式文件的工具

下载`ffmpeg`

```bash
# 安装ffmpeg
brew install ffmpeg
```

注册一个脚本

```bash
concat download-bv "temp_content" 
```

下载`BBDown`到`plugin`文件夹，下载`filetranslate`到`plugin`到文件夹

找到`script`目录下的`download-bv.sh`，写入以下脚本：

```bash
#!/bin/bash
cd /Applications/work_copilot_java/plugin

# 等待用户输入BV号
read -p "请输入B站视频BV号: " bv

if [ -z "$bv" ]; then
    echo "错误：未提供 bv 参数"
    echo "用法: $0 <bv号>"
    read -p "按任意键退出..." -n1 -s
    exit 1
fi

echo "pwd res: $(pwd), bv: ${bv}"
echo "call BBDown..."
./BBDown --audio-only ${bv}
echo "BBDown work done"
echo "current directory contain:"
ls -al
echo "call filetranslate convert..."
mkdir -p ../output
./filetranslate -if m4a -of flac -o ../output -d
echo "afer directory contain:"
ls -al
# 添加结束暂停
echo ""
echo "所有操作已完成"
read -p "按任意键进入文件目录..." -n1 -s
open ../output
```

执行

```bash
j download-bv
```

输入想要下载的b站视频的bv号即可



# Contact

作者邮箱： 3065225677@qq.com