# OTB Project Documentation
### Understanding and Modifying Configuration Files

## Table of Contents


- [Version](#version)
	- [Changelog](#changelog)
- [Warning](#warning)
- [Account](#account)
	- [Path](#path)
	- [Sample File](#sample-account-configuration-file)
	- [Fields](#fields)
- [General Config](#general-config)
 	- [Path](#path-1)
	- [Sample File](#sample-general-configuration-file)
	- [Fields](#fields-1)
- [Bot Config](#bot-config)
	- [Path](#path-2)
	- [Sample File](#sample-bot-configuration-file)
	- [Fields](#fields-2)
- [Channel Config](#channel-config)
	- [Path](#path-3)
	- [Sample File](#sample-channel-configuration-file)
	- [Fields](#fields-3)

## Version

Version 0.1.0

#### Changelog

* 0.1.0 - Account, general config, bot config, and channel config documentation added

## Warning

Be careful when modifying configuration files. If a configuration file is not in valid JSON format when it is loaded, it will be overwritten with the default configuration file. You can check whether JSON is formatted correctly <a href="http://jsonlint.com/" target="_blank">here</a>.

## Account

#### Path

The account configuration file can be found at:
```
.otbproject/config/account.json
```

#### Sample Account Configuration File

```json
{
  "name" : "your_name_here",
  "oauth" : "oauth:some_characters_here"
}
```

#### Fields

| Field | Description |
|:-----------|:------------|
|`name`|The name of the Twitch account being used for the bot.|
|`oauth`|An oauth token associated with the Twitch account. You can generate an oauth token for your account <a href="http://twitchapps.com/tmi/" target="_blank">here</a>. (Note: generating a new oauth token voids any previous token)|

## General Config

#### Path

The general configuration file can be found at:
```
.otbproject/config/general-config.json
```

#### Sample General Configuration File

```json
{
  "portNumber" : 80,
  "ip_binding" : "0.0.0.0",
  "permanently_enabled_commands" : [ "!bot-enable-meta" ]
}
```

#### Fields

| Field | Description |
|:-----------|:------------|
|`portNumber`||
|`ip_binding`||
|`permanently_enabled_commands`|A list of commands which the bot will listen for even when it is disabled.|

## Bot Config

#### Path

The bot configuration file can be found at:
```
.otbproject/data/bot-channel/bot-config.json
```

#### Sample Bot Configuration File

```json
{
  "channelJoinSetting" : "NONE",
  "whitelist" : [ ],
  "blacklist" : [ ],
  "currentChannels" : [ ],
  "messageSendDelayInMilliseconds" : 1600
}
```

#### Fields

| Field | Description |
|:-----------|:------------|
|`channelJoinSetting`|Controls how the bot is allowed to join channels when the `!join` command is run in the bot's channel. Valid options for the `channeJoinSetting` are: `"NONE"`, `"WHITELIST"`, and `"BLACKLIST"`.<br>`"WHITELIST"` allows the bot to join only channels listed in the `whitelist`.<br>`"BLACKLIST"` prevents the bot from joining channels listed in the `blacklist`.<br>`"NONE"` allow the bot to join any channel.|
|`whitelist`|A list of channels the bot is allowed to join if the `channelJoinSetting` is `"WHITELIST"`|
|`blacklist`|A list of channels the bot is not allowed to join if the `channeJoinSetting` is `"BLACKLIST"`|
|`currentChannels`|The channels the bot was in when it was last shut down. These channels will be joined when the bot starts.|
|`messageSendDelayInMilliseconds`|The minimum delay in milliseconds between when the bot can send two messages in a channel. Be careful modifying this value, as the bot may send too many messages in too short a duration if it is not sufficiently high. If the bot sends too many messages too quickly, it may be IP banned from Twitch for 8 hours.|

## Channel Config

#### Path

The configuration file for a given channel can be found at:
```
.otbproject/data/channels/<channel name>/config.json
```

#### Sample Channel Configuration File

```json
{
  "commandCooldown" : 10,
  "userCooldowns" : {
    "ul_internal" : 0,
    "ul_broadcaster" : 0,
    "ul_super_moderator" : 0,
    "ul_moderator" : 0,
    "ul_regular" : 15,
    "ul_subscriber" : 30,
    "ul_default" : 30
  },
  "debug" : false,
  "enabled" : true,
  "queueLimits" : {
    "highPriorityLimit" : -1,
    "defaultPriorityLimit" : 5,
    "lowPriorityLimit" : 0
  }
}
```

#### Fields

| Field | Description |
|:-----------|:------------|
|`commandCooldown`||
|`userCooldowns`|See the table below.|
|`debug`||
|`enabled`||
|`queueLimits`|See the table below.|

##### userCooldowns

| Field | Description |
|:-----------|:------------|
|`ul_internal`||
|`ul_broadcaster`||
|`ul_super_moderator`||
|`ul_moderator`||
|`ul_regular`||
|`ul_subscriber`||
|`ul_default`||

##### queueLimits

| Field | Description |
|:-----------|:------------|
|`highPriorityLimit`||
|`defaultPriorityLimit`||
|`lowPriorityLimit`||
