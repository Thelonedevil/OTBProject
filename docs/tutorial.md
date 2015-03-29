# OTB Project Documentation
### Tutorial: How to Run a Bot

## Table of Contents
 - [Version](#version)

## Version
Version 1.0

Corresponds to release 1.0.0

## Tutorial

#### Starting Out

Before being able to modify configurations or other data for the bot, you need to run it once so that it can set up all the files it needs. It will not actually connect to Twitch however, because it does not have your account information, so you can stop the bot after the window pops up. It may take several seconds for the window to pop up the first time you run the bot.

#### Finding Your Installation Directory

After running the bot once, it will have set up a directory to store data for the bot. The default installation directory is a folder called `.otbproject` in your home directory. On Windows, you can find your default installation directory by typing `%USERPROFILE%\.otbproject` into the address bar of Windows Explorer (which is the file explorer, and is not the same as Internet Explorer). I assume that if you are on Mac or Linux, you know where your home directory is.

After this point, all paths will be denoted within the `.otbproject` installation directory, and it is assumed that you have already determined that directory's location.

#### Setting Your Account Information

You can find the account configuration file at `.otbproject/config/account.json`. Set your username and oauth token in the file, preferably while the bot is not running, and save the file. When you next run the bot, it will use that username and oauth token. See the [config documentation](config-documentation.md#account) for more information.


