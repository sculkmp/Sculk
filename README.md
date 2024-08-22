<div align="center">
<img src="https://static.wikia.nocookie.net/minecraft_gamepedia/images/e/e2/Sculk_%28pre-release%29.png" width="150" height="150" alt="Logo Sculk">
<h4>Open source server software for Minecraft: Bedrock Edition written in Java</h4>

[![SculkVersion](https://img.shields.io/badge/version-soon-14191E.svg?cacheSeconds=2592000)]()
[![MinecraftVersion](https://img.shields.io/badge/minecraft-v1.21.21%20(Bedrock)-17272F)]()
[![ProtocolVersion](https://img.shields.io/badge/protocol-712-38D3DF)]()
[![Github Download](https://img.shields.io/github/downloads/sculkmp/Sculk/total?label=downloads%40total)]()
[![License](https://img.shields.io/badge/License-LGPL--3-yellow.svg)]()
[![JitPack](https://jitpack.io/v/sculkmp/Sculk.svg)]()

</div>

## 📖 Introduction
Sculk is open source server software for Minecraft: Bedrock Edition, It has a few key advantages over other server software:

## 🎯 Features
* Written in Java, Sculk is faster and more stable.
* We provided a high-level friendly API akin PocketMine plugin developers. Save yourself the hassle of dealing with the dot-and-cross of the low-level system API and hooks, we've done the difficult part for you!

## ✨ Creating plugins
Add Sculk to your dependencies *(it is hosted by JitPack, so you need to specify a custom repository)*.

For maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.github.sculkmp</groupId>
        <artifactId>Sculk</artifactId>
        <version>Tag</version>
    </dependency>
</dependencies>
```
For gradle:
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.sculkmp:Sculk:Tag'
}
```

| Milestone                                  | Status |
|--------------------------------------------|--------|
| **⚒️ Construction of the server tree**     | ✅     |
| **👓 Visible server**                      | ✅     |
| **🛜 Join server**                         | ⏳     |
| **🎍 World loader**                        | 🚧     |
| **🔌Plugin loader**                        | ⏳     |
| **⌨️ Command System**                      | 🚧     |
| **🔐 Permission System**                   | 🚧     |
| **🎈 Event System**                        | ⏳     |
| **🖼 Scoreboard API**                      | 🚧     |
| **🖼 Form API**                            | ✅     |
| **👤 Player & Actor API**                  | ⏳     |
| **🔩 Item API**                            | 🚧     |
| **🧱 Block API**                           | 🚧     |
| **📦 Inventory API**                       | 🚧     |
| **🔬 Beta Testing & Community Feedback**   | 🚧     |
| **🚀 Official Release & Support**          | 🚧     |

Here's a legend to guide you:
- ✅: Task is completed. Woohoo! 🎉
- 🚧: Task is under way. We're on it! 💪
- ⏳: Task is up next. Exciting things are coming! 🌠

## ⚒️ Build JAR file
- `git clone https://github.com/sculkmp/Sculk`
- `cd Sculk`
- `git submodule update --init`
- `mvn clean package`
The compiled JAR can be found in the `target/` directory.

## 🚀 Running
Simply run `java -jar Sculk-1.0-SNAPSHOT-jar-with-dependencies.jar`

## 🙌 Contributing
We warmly welcome contributions to the Sculk project! If you are excited about improving Minecraft 
Bedrock server software with Java, here are some ways you can contribute:

### Reporting bugs
If you encounter any bugs while using Sculk, please open an [issue](https://github.com/sculkmp/Sculk/issues) in
our GitHub repository. Ensure to include a detailed description of the bug and steps to reproduce it.

### Submitting a Pull Request
We appreciate code contributions. If you've fixed a bug or implemented a new feature, please submit a pull request!
Please ensure your code follows our coding standards and include tests where possible.

## 📌 Licensing information
This project is licensed under LGPL-3.0. Please see the [LICENSE](/LICENSE) file for details.

`sculkmp/Sculk` are not affiliated with Mojang. 
All brands and trademarks belong to their respective owners. Sculk is not a Mojang-approved software, 
nor is it associated with Mojang.