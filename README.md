<div align="center">
  <h1 align="center">DevBot</h1>
  <a href="https://github.com/pitzzahh/pitzzahh-bot">
    <img src="https://iili.io/ZOtwDF.md.jpg" alt="Logo" style="border-radius: 50%" width="100" height="100">
  </a>
<h3 align="center">A Discord Bot made using Java Discord API</h3>
</div>

---

![GitHub Issues](https://img.shields.io/github/issues/pitzzahh/pitzzahh-bot)
![Forks](https://img.shields.io/github/forks/pitzzahh/pitzzahh-bot)
![Stars](https://img.shields.io/github/stars/pitzzahh/pitzzahh-bot)
![License](https://img.shields.io/github/license/pitzzahh/pitzzahh-bot)
![Forks](https://img.shields.io/github/forks/pitzzahh/pitzzahh-bot)
![Stars](https://img.shields.io/github/stars/pitzzahh/pitzzahh-bot)
![Contributors](https://img.shields.io/github/contributors/pitzzahh/pitzzahh-bot)
![Last Commit](https://img.shields.io/github/last-commit/pitzzahh/pitzzahh-bot)
![Code size](https://img.shields.io/github/languages/code-size/pitzzahh/pitzzahh-bot)
![Top language](https://img.shields.io/github/languages/top/pitzzahh/pitzzahh-bot)
![Languages count](https://img.shields.io/github/languages/count/pitzzahh/pitzzahh-bot)
![Repo size](https://img.shields.io/github/repo-size/pitzzahh/pitzzahh-bot)
![Lines of code](https://img.shields.io/tokei/lines/github/pitzzahh/pitzzahh-bot?label=lines%20of%20code)

---

A discord bot for you to use in your personal servers or a community.
This Discord bot is built using [Java Discord API (JDA)](https://jda.wiki/) with [Spring](https://spring.io/) framework integration. It contains several features, including:

- **User Verification**: Ensure that only authorized users access the server by allowing them to verify their accounts.
- **Random Math Problem Game**: Take a quick break and solve some math problems.
- **Message Checker**: Ensure a safe environment for you and your friends or community by filtering vurlgar words.
- **Slash Commands**: This bot also includes several slash commands, see all the [list of commands](#features) available.

> 📘 Please take note
>
> You can invite the bot using this [link](https://discord.com/api/oauth2/authorize?client_id=1077238079083008051&permissions=8&scope=applications.commands%20bot). Feel free to make the necessary changes that suits you and your community's needs.

## Setting up the bot

### Pre-requisites

Before the bot can run, ensure that you have all the boxes checked in the requirements.

- [x] A bot token generated from the [Discord Developer Portal](https://discord.com/developers/applications) site with Administrative privileges.
- [x] [Git](https://git-scm.com/) must be installed in your system.
- [x] [Java JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) Version 17+ is required.
- [x] An IDE such as [IntelliJ IDEA](https://www.jetbrains.com/idea/) or a Code Editor like [VS Code](https://code.visualstudio.com/) must be installed.

### Running the bot

1. Clone this repository to your local machine.
2. Open the project in your IDE or code editor.
3. Open the `application.yml.examlple` file located in `src/main/java/resources/`.
4. Remove the `.example` extension the `application.yml.example` file.
5. Copy your bot token and paste it in the token section.
6. Run the app.

### Contributing to the project

1. Follow the instruction in section [Running the bot](#running-the-bot).
2. Make the necessary changes such as but not limited to:
    - Fixing bugs/errors.
    - Adding new features.
    - Optimize code.
    - Testing and reporting issues/errors
3. Open a pull request or an issue.

## Features

Here are some list of commands available to use.

| Command         | Description                                                                |
| --------------- | -------------------------------------------------------------------------- |
| `/play`         | Play a game from the choices available and select its level of difficulty. |
| `/joke`         | Get a random joke to enlighten your day.                                   |
| `/confess`      | Confess something, may it be romantically or more general.                 |
| `/submit-joke`  | Submit a joke to the bot (Will be approved by authorized users).           |
| `/approve-joke` | Approve a submitted joke to the bot (Only authorized).                     |

## Credits

This bot was created by [Peter John Arao](https://github.com/pitzzahh).

## License

This project is licensed under the MIT license. See the [LICENSE](LICENSE) file for more information.
