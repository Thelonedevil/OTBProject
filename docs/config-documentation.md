# OTB Project Documentation
### Understanding and Modifying Configuration Files

## Table of Contents


- [Version](#version)
	- [Changelog](#changelog)
- [Account](#account)
- [General Config](#general-config)
- [Bot Config](#bot-config)
- [Channel Config](#channel-config)

## Version

Version 0.1.0

#### Changelog

* 0.1.0 - Account, general config, bot config, and channel config documentation added

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
  "permanently_enabled_commands" : [ ]
}
```

#### Fields

| Field | Description |
|:-----------|:------------|
|`portNumber`||
|`ip_binding`||
|`permanently_enabled_commands`||

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
|`channelJoinSetting`||
|`whitelist`||
|`blacklist`||
|`currentChannels`||
|`messageSendDelayInMilliseconds`||

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
|`userCooldowns`||
|`debug`||
|`enabled`||
|`queueLimits`||

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
