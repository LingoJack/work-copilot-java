
🚀 CLI 使用说明：**work-copilot**（即 **j** 字母小写）

---

#### **单次使用方法（较慢）**  
使用命令：  
```bash
j <option> <necessary_arguments>
```

---

#### **多次使用方法（快速模式）**  
使用命令进入 **copilot 状态**，无需重复输入开头的 `j` 指令，且速度更快：  
```bash
j
```

---

### 可用选项：
以下部分均指 **path**, **browser**, **editor** 等配置文件属性。

---

### 常见命令

---

#### 🚪 **退出**
**可用命令**：`exit`, `-exit`, `-quit`, `quit`, `-q`  
**示例**：  
```bash
j exit
```

---

#### 🔑 **增加别名**
**可用命令**：`set`, `-set`, `s`  
**示例**：  
```bash
j set <alias> <path>
```

---

#### 📋 **列举所有别名**
**可用命令**：`ls`, `list`, `-list`  
**示例**：  
```bash
j ls [part | all]
```

---

#### ℹ️ **查看版本信息**
**可用命令**：`-version`, `version`, `v`  
**示例**：  
```bash
j version
```

---

#### ✏️ **修改别名的路径**
**可用命令**：`mf`, `-modify`, `modify`  
**示例**：  
```bash
j mf <alias> <new_path>
```

---

#### 🗑️ **移除别名**
**可用命令**：`rm`, `-remove`, `remove`  
**示例**：  
```bash
j rm <alias>
```

---

#### 🌐 **标记为（浏览器，编辑器或 VPN）**
**可用命令**：`nt`, `note`, `-note`  
**示例**：  
```bash
j nt <alias> <category(browser, editor, vpn, outer-url)>
```

---

#### 🚫 **解除标记（浏览器，编辑器或 VPN）**
**可用命令**：`dnt`, `denote`, `-denote`  
**示例**：  
```bash
j dnt <alias> <category(browser, editor, vpn, outer-url)>
```

---

#### ✨ **重命名别名**
**可用命令**：`rename`, `-rename`, `rn`  
**示例**：  
```bash
j rename <alias> <new_name>
```

---

#### 📜 **切换详细输出模式**
```bash
j log mode verbose
```

#### 🧑‍💻 **切换简洁输出模式**
```bash
j log mode concise
```

---

#### 🔍 **查找指令**
**可用命令**：`find`, `contain`  
**示例**：  
```bash
j find alias
```
其中 **part** 可为 `path`, `browser`, `editor` 等。

---

#### ➗ **拼接脚本指令**
```bash
j concat <script_name> "<content>"
```

---

#### 📝 **输入日报内容**
**可用命令**：`r`, `report`  
**示例**：  
```bash
j report "content"
```
> **注意**：若内容中没有空格，双引号可以省略。

---

#### 📅 **查看日报内容**
**可用命令**：`c`, `check`  
**示例**：  
```bash
j check [tail_line_count]
```

---

#### 🔍 **查看程序性能使用情况** 
**可用命令**：`ps`, `system`  
**示例**：
```bash
j ps
```

---

### 配置文件说明

除了通过命令操作别名和路径外，还可以直接在配置文件 **`conf.ini`** 内进行修改。注意以下几点：

#### **Path**
- 存储所有 **key-value 形式的别名-路径** 配置。

#### **Version**
- 存储 **版本信息**。

#### **InnerUrl**
- 国内网站别名-网址的键值对，打开国内网站不会自动打开 VPN。

#### **OuterUrl**
- 国外网站别名-网址的键值对，打开国外网站会先打开 VPN，别名被标记为 `OuterUrl` 后会加入到此处。

#### **Editor**
- 存储被标记为 **编辑器** 的别名，支持选择打开的文件/目录。

#### **Browser**
- 存储被标记为 **浏览器** 的别名，支持快速通过网址别名打开网站，`bs` 为默认浏览器。

#### **Vpn**
- 存储被标记为 **VPN** 的别名，打开国外网站时会先启动 VPN。

#### **Script**
- 默认存储在安装目录的 `script` 目录中。删除脚本的别名时会自动删除脚本文件。

#### **Report**
- 使用周报功能前，需要在配置文件 `report.week_report` 中提前配置周报文件路径。

---

### 使用前置条件

1. 需要 **Java 环境** 并正确配置 **Java 环境变量**。
2. 将安装目录下的 **bin** 目录添加到 **环境变量** 中。

---

### 路径注意事项

1. **路径中不应包含空格**：所有别名对应的路径不应带有空格，否则可能被命令行错误识别。
   - **解决方案**：为路径包含空格的应用创建快捷方式，将快捷方式剪切到无空格的路径下，并将快捷方式文件的地址复制到 `application.yml` 配置文件中。粘贴路径时请删除引号。

---

如果你有任何问题或需要进一步的帮助，随时告诉我！😊
作者邮箱：
3065225677@qq.com