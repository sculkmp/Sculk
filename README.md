<div align="center">
<img src="https://static.wikia.nocookie.net/minecraft_gamepedia/images/e/e2/Sculk_%28pre-release%29.png" width="150" height="150" alt="Logo Sculk">
<h4>Open source server software for Minecraft: Bedrock Edition written in Java</h4>

[![SculkVersion](https://img.shields.io/badge/version-soon-14191E.svg?cacheSeconds=2592000)]()
[![MinecraftVersion](https://img.shields.io/badge/minecraft-v1.21.51%20(Bedrock)-17272F)]()
[![ProtocolVersion](https://img.shields.io/badge/protocol-766-38D3DF)]()
[![Github Download](https://img.shields.io/github/downloads/sculkmp/Sculk/total?label=downloads%40total)]()
[![License](https://img.shields.io/badge/License-LGPL--3-yellow.svg)]()
[![JitPack](https://jitpack.io/v/sculkmp/Sculk.svg)]()

</div>

## 📖 Introduction

Sculk is a high-performance server software for Minecraft: Bedrock Edition, written entirely in Java. It aims to provide a robust and feature-rich alternative to existing server implementations, with a focus on scalability and ease of use.

> [!WARNING]
> Sculk is currently under development and is not yet ready for production use. We are actively working on implementing core features and improving stability. If you would like to contribute to the project, please see the [Contributing](#-contributing) section below.

## ✏️ Getting Started

To start using **Sculk**, add it to your project dependencies. Since **Sculk** is hosted on JitPack, you need to add the JitPack repository to your build configuration.

### Maven
Add the following to your `pom.xml` file:

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

### Gradle
Add the following to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.sculkmp:Sculk:Tag'
}
```

## 🚀 Project Milestones

| Milestone                                | Status  |
|------------------------------------------|---------|
| **⚒️ Server Tree Construction**          | ✅ Completed |
| **👓 Visible Server**                    | ✅ Completed |
| **🛜 Server Joining**                    | ✅ Completed |
| **🎍 World Loader**                      | 🚧 In Progress |
| **🔌 Plugin Loader**                     | ✅ Completed |
| **⌨️ Command System**                    | ⏳ Up Next |
| **🔐 Permission System**                 | ⏳ Up Next |
| **🎈 Event System**                      | ⏳ Up Next |
| **🖼 Scoreboard API**                    | 🚧 In Progress |
| **🖼 Form API**                          | ✅ Completed |
| **👤 Player & Actor API**                | ⏳ Up Next |
| **🔩 Item API**                          | 🚧 In Progress |
| **🧱 Block API**                         | 🚧 In Progress |
| **📦 Inventory API**                     | 🚧 In Progress |
| **🔬 Beta Testing & Community Feedback** | 🚧 In Progress |
| **🚀 Official Release & Support**        | 🚧 In Progress |


## ⚒️ Build JAR file from source
1. `git clone https://github.com/sculkmp/Sculk`
2. `cd Sculk`
3. `git submodule update --init`
4. `mvn clean package`

- ✅ The compiled JAR can be found in the `target/` directory.

## 🚀 Running
To run the compiled JAR file, use the following command:
```bash
java -jar Sculk-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 🙌 Contributing
We warmly welcome contributions to the Sculk project! If you are excited about improving Minecraft 
Bedrock server software with Java, here are some ways you can contribute:

![contributors](https://contrib.rocks/image?repo=sculkmp/Sculk)

## 📌 Licensing information
This project is licensed under LGPL-3.0. Please see the [LICENSE](/LICENSE) file for details.

---

> [!CAUTION]
> `sculkmp/Sculk` are not affiliated with Mojang.
> All brands and trademarks belong to their respective owners. Sculk is not a Mojang-approved software,  
> nor is it associated with Mojang.  
> It is always possible to use the Bedrock Dedicated Server with this link:
> [Bedrock Server](https://www.minecraft.net/en-us/download/server/bedrock)
