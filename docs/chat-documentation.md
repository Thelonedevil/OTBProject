#OTB Project Documentation
###Interacting with a bot in chat

##Table of Contents


- [Version](#version)
	- [Changelog](#changelog)
- [Commands](#commands)
	- [Built-in Channel Commands](#built-in-channel-commands)
	- [Built-in Bot-Channel Commands](#built-in-bot-channel-commands)
	- [Flags](#flags)
	- [User Levels](#user-levels)
	- [Aliases](#aliases)
	- [Built-in Aliases](#built-in-aliases)
	- [Command Responses](#command-responses)
- [Special Terms](#special-terms)
	- [Terms](#terms)
	- [Modifiers](#modifiers)
	- [Embedded Strings](#embedded-strings)
	- [Examples](#examples)

##Version

Version 0.2.0 [WIP]

####Changelog

* 0.2.0
  - Added descriptions of commands that come with release
  - Added more terms to the parser
* 0.1.2
  - Special terms can contain spaces in defaults and other embedded strings
  - More special terms added
* 0.1.1
  - Changed syntax for special terms and defaults
* 0.1.0
  - Described special terms and their modifiers; gave examples of both

##Commands

Commands are words which the bot will respond to if they are the first word in a message. Commands often start with an exclamation mark, such as `!example`, but they can be any string of characters without a space in them. Some commands can tell the bot to perform an action, instead of just printing a message in response; these are known as "script commands".

#### Built-in Channel Commands

Each channel by default has script commands to add and remove commands and aliases, as well as script commands to do several other things. The built-in channel script commands are below. Flags and user levels are covered later.

| Command | Flags | Arguments | Description |
|:--------|:------|:----------|:------------|
|`!command` `add | new`|`ul`, `ma`|`<command>` `<response>`|Adds a command which did not previously exist. The command is the first space-delineated word (after any flags), and the response is everything following it.|
|`!command` `set | edit`|`ul`, `ma`|`<command>` `<response>`|Sets the specified command with the given response, whether it existed before or not. The command is the first space-delineated word (after any flags), and the response is everything following it.|
|`!command` `remove | delete | rm | del`||`<command>`|Removes the specified command.|
|`!command` `list`|||Lists all commands (other than the built-in response commands) without their responses.|
|`!command` `raw`||`<command>`|Prints the response for the specified command.|
|`!command` `enable`||`<command>`|Enables the specified command.|
|`!command` `disable`||`<command>`|Disables the specified command.|
|`!alias-meta` `add | new`||`<alias>` `<command>`|Adds an alias which did not previously exist. The alias is the first space-delineated word, and the command is everything following it (may contain spaces).|
|`!alias-meta` `set`||`<alias>` `<command>`|Sets the specified alias to the given command, whether it existed before or not. The alias is the first space-delineated word, and the command is everything following it (may contain spaces).|
|`!alias-meta` `remove | delete | rm | del`||`<alias>`|Removes an alias.|
|`!alias-meta` `list`|||Lists all aliases without their commands.|
|`!alias-meta` `getCommand`||`<alias>`|Prrints the command to which an alias is aliased.|
|`!alias-meta` `enable`||`<alias>`|Enables the specified alias.|
|`!alias-meta` `disable`||`<alias>`|Disables the specified alias.|
|`!setExecUL`||`<command>` `<user level>`|Sets the minimum user level to execute the specified command.|
|`!setMinArgs`||`<command>` `<min args>`|Sets the minimum number of arguments with which a command must be run. Cannot be negative.|
|`!rename`||`<old command name>` `<new command name>`|Renames a command. A command with the new name cannot already exist.|
|`!resetCount`||`<command>`|Resets the count of the specified command to 0.|
|`!assignUserLevel`||`<user>` `<user level>`|Assigns the specified user level to the specified user.|
|`!silence-meta` `on | true`|||Silences the bot in the channel. Commands which run scripts will still execute the scripts, but no responses will be printed in chat.|
|`!silence-meta` `off | false`|||Unsilences the bot in the channel. Responses will be printed again.|
|`!bot-enable-meta` `false`|||Disables the bot in the channel. It will not respond to any commands (both by running scripts and printing responses in chat) except the command to enable it.|
|`!bot-enable-meta` `true`|||Enables the bot in the channel.|
|`!leave`||`<bot name>`|Makes the bot leave the channel. Its name must be specified in case other bots are running in the channel (such as the Monstercat bot), and you want a different bot to leave.|

#### Built-in Bot-Channel Commands

The bot's channel has its own script commands to perform actions specific to the bot, such as joining channels. The built-in bot-channel script commands are below.

| Command | Flags | Arguments | Description |
|:--------|:------|:----------|:------------|
|`!join`|||Joins the channel of the user who ran the command. Respects the join mode, as specified below and in the [config documentation](config-documentation.md#bot-config).|
|`!joinMode`||`<mode>`||
|`!whitelist` `add`||`<channel>`||
|`!whitelist` `remove`||`<channel>`||
|`!whitelist` `list`||||
|`!blacklist` `add`||`<channel>`||
|`!blacklist` `remove`||`<channel>`||
|`!blacklist` `list`||||

#### Flags

Flags are optional arguments for a command which give more control over what the command does or how it executes. Any flags mentioned above are listed here.

| Flag | Usage | Option(s) | Description |
|:-----|:------|:----------|:------------|
|`ul`|`--ul=<option>`|A user level marker as described in the table below||
|`ma`|`--ma=<option>`|An integer greater than 0||

#### User Levels

| User Level | Marker(s) |
|:-----------|:----------|
|Default|`default | def | none | any | all`|
|Subscriber|`subscriber | sub`|
|Regular|`regular | reg`|
|Moderator|`moderator | mod`|
|Super Moderator|`super-moderator | super_moderator | smod | sm`|
|Broadcaster|`broadcaster | bc`|

Additionally, there is the user level 'Ignored', which can be used with `!assignUserLevel`, but not with the `ul` flag. You can also reset a user's user level to whatever it would be using Twitch alone, as described below.

| User Level | Marker(s) |
|:-----------|:----------|
|Ignored|`ignored | ig`|
|Reset|`reset | twitch`|

The user levels 'Subscriber', 'Moderator', and 'Broadcaster' cannot be assigned using `!assignUserLevel` because they are user properties determined by Twitch.

#### Aliases

An alias is a word which represents another word or group of words. Like commands, aliases are only processed by the bot if they are the first word of a message. However, an alias can expand into a command and parameters for the command, to make running certain commands easier. If an alias expands to something which is not a command, it won't do anything.

Aliases will not loop; they will only be expanded once. For example, if you alias `!foo` to `!bar` and `!bar` to `!foo test`, it will process the message `!foo example` into `!foo test example`, and then check if `!foo` is a command to run.

#### Built-in Aliases

| Alias | Command |
|:------|:--------|
|`!commands`|`!command`|
|`!addcom`|`!command add`|
|`!delcom`|`!command delete`|
|`!setcom`|`!command set`|
|`!editcom`|`!command set`|
|`!enable`|`!command enable`|
|`!disable`|`!command disable`|
|`!alias`|`!alias-meta add`|
|`!unalias`|`!alias-meta remove`|
|`!aliaslist`|`!alias-meta list`|
|`!resetUserLevel`|`!assignUserLevel reset`|
|`!silence`|`!silence-meta true`|
|`!unsilence`|`!silence-meta false`|
|`!bot-enable`|`!bot-enable-meta true`|
|`!enable-bot`|`!bot-enable-meta true`|
|`!bot-disable`|`!bot-enable-meta false`|
|`!disable-bot`|`!bot-enable-meta false`|

#### Command Responses

Script commands send responses to chat as commands, not directly as messages. The commands which they use to respond are listed below. You can change the responses of the commands below to customize the responses of script commands. For example, if you don't like what the bot says when it creates a command, you can change it by changing the response to `~%command.set.success`.

| Command | Description | Default Response |
|:--------|:------------|:-----------------|
|`~%general:insufficient.args`|||
|`~%general:insufficient.user.level`|||
|`~%general:invalid.arg`|||
|`~%general:invalid.flag`|||
|`~%command.general:does.not.exist`|||
|`~%command.add.already.exists`|||
|`~%command.set.success`|||
|`~%command.is.alias`|||
|`~%command.remove.success`|||
|`~%command.enabled`|||
|`~%command.disabled`|||
|`~%alias.general:does.not.exist`|||
|`~%alias.add.already.exists`|||
|`~%alias.success`|||
|`~%unalias.success`|||
|`~%alias.enabled`|||
|`~%alias.disabled`|||
|`~%command.set.exec.ul.success`|||
|`~%command.set.min.args.success`|||
|`~%command.reset.count.success`|||
|`~%command.rename.success`|||
|`~%command.rename.already.in.use`|||
|`~%bot.unsilence.success`|||
|`~%bot.enable.success`|||
|`~%bot.leave.need.name`|||
|`~%bot.joined.channel`|||
|`~%assign.user.level.success`|||
|`~%reset.user.level.success`|||
|`~%assign.unsupported.user.level`|||
|`~%join.mode.set`|||
|`~%whitelist.add.success`|||
|`~%whitelist.remove.success`|||
|`~%blacklist.add.success`|||
|`~%blacklist.remove.success`|||

##Special Terms

There are some special terms which can be used when creating a command to create a response which varies depending on how the command is run. Each special term is replaced by some value, as described in the following table. Some examples of special term usage are shown at the end of the section.

####Terms

| Term | Description |
|:-----|:------------|
|`[[user]]`|The name of the user who ran the command.|
|`[[args]]` or `[[args{{default}}]]`|All arguments with which the command was run (separated by spaces). If the command is run without arguments, and a default is given, the default is used. The default can contain another special term. If no default is given, it is replaced by an empty string (it is left blank).|
|`[[argN]]` or `[[argN{{default}}]]`|The `N`th argument, where `N` is a whole number greater than 0. If the command is run with fewer than `N` arguments, and a default is given, the default is used. The default can contain another special term. If no default is given, it is replaced by an empty string (it is left blank).|
|`[[fromargN]]` or `[[fromargN{{default}}]]`|All the arguments from the `N`th argument and on. As with `[[argN]]`, `N` must be a whole number. If `N` is 1, it is equivalent to `[[args]]`.|
|`[[numargs]]`|The number of arguments with which the command was run.|
|`[[channel]]`|The channel in which the command was run.|
|`[[count]]`|The number of times the command has been run since it was created or since it was last modified.|
|`[[quote]]`|A random quote from the quote database. The quote database is not yet implemented.|
|`[[game]]`|The name of the game being played (as listed above the twitch stream). Not yet implemented.|

####Modifiers

Modifiers can be used to modify the string with which the term is replaced. Modifiers are used in the following format:

```
[[term.modifier]]
[[term.modifier{{string}}]]
```

The modifiers are described in the following table.

| Modifier | Description |
|:---------|:------------|
|`lower`|The string is made entirely lowercase.|
|`upper`|The string is made entirely uppercase.|
|`first_cap`|The first letter of the string is made uppercase. All other letters are made lowercase.|
|`word_cap`|The first letter of each word (separated by a space) in the string is made uppercase. All other letters are made lowercase. Identical to first_cap for anything which is a single word (e.g. `[[user]]`, `[[argN]]`).|
|`first_cap_soft`|The first letter of the string is made uppercase. All other letters are not changed.|
|`word_cap_soft`|The first letter of each word (separated by a space)  in the string is made uppercase. All other letters are not changed. Identical to `first_cap_soft` for anything which is a single word (e.g. `[[user]]`, `[[argN]]`).|

####Embedded Strings

Some terms can contain embedded strings. The `{{default}}` in the `[[args]]` and `[[argN]]` terms is an example of an embedded string. Embedded strings are surrounded by `{{` and `}}`. Embedded strings can be useful for providing more information or options for a term, and are used extensively in more advanced and versatile terms.

If you are already confused at this point, you should probably skip this section and move onto the examples of term usage below.

The following are terms which are significantly more useful when given embedded strings, or not useful at all without them. If less than N embedded strings are provided, then any term which attempts to use the Nth embedded string will treat it as an empty string.

| Term | Description |
|:-----|:------------|
|`[[ifargs{{yes_args}}{{no_args}}]]`|Prints the first embedded string string if at least one argument was given. If no arguments were given, it prints the second embedded string. For either condition, if no string is supplied, it does nothing.|
|`[[ifargN{{N_args}}{{not_N_args}}]]`|Prints the first embedded string if at least `N` arguments were given. If less than `N` arguments were given, it prints the second embedded string. For either condition, ff no string is supplied, it does nothing. `N` must be greater than 0.|
|`[[foreach{{prepend}}{{append}}]]`|For each argument provided, prints the string specified as `prepend`, the argument, and then the string specified as `append`. If a modifier (described later) is used, it is applied to each argument (but not to `prepend` or `append`). If only one string is provided, it is `prepend`. If both are left out, the arguments are printed unmodified (and without spaces).|
|`[[equal{{compare1}}{{compare2}}{{same}}{{different}}]]`|If the first two embedded strings are the same, then it prints the third embedded string. If not, it prints the fourth embedded string. Not particularly useful if neither of the first two embedded strings are a term.|

####Examples

Here are some examples of special terms and modifiers:

**Commands**

| Name | Response |
|:-----|:---------|
|!hello|Hello [[arg1]]!|
|!poke|[[user]] pokes [[arg1{{someone}}]].|
|!hug|[[user.first_cap]] hugs [[args{{everyone}}]]!|
|!gc|[[user.upper]] FIRES THE GLITTER CANNON!|
|!riot|༼ つ ◕\_◕ ༽つ [[args.upper{{SHOUT}}]] OR RIOT ༼ つ ◕\_◕ ༽つ|
|!cookies|/me throws a cookie at [[arg1]] because master [[user]] said so! (Cookies given: [[count]])|
|!foo|[[arg1.lower]] [[arg1.upper]] [[arg1.word_cap]] [[arg1.word_cap_soft]]|
|!bar1|[[args.first_cap]]|
|!bar2|[[args.word_cap]]|
|!bar3|[[args.first_cap_soft]]|
|!bar4|[[args.word_cap_soft]]|
|!words|words [[arg1{{something}}]] words [[arg2{{[[arg1]]}}]] words [[arg3{{[[arg2{{[[arg1{{dunno}}]]}}]]}}]] words [[arg4{{[[arg2]]}}]] words|
|!multi|Watch [[foreach{{}}{{, }}]] and me at the same time! example.com/multistream/[[channel]]/[[foreach.lower{{}}{{/}}]]|
|!ifargs|A secret message appears if you run this command with args.[[ifargs{{ I mean, I suppose it's a secret, but it's not a very interesting one :P}}]]|
|!ifarg2|More args are [[ifarg2{{not }}]]needed. You need[[ifarg2{{ed}}]] 2 args.|

**Running the commands in chat**

All commands are run by a user named “fred”, and responded to by a bot named “Bot”.

| Message in chat | Response |
|:----------------|:---------|
|fred: !hello bob|Bot: Hello bob!|
|fred: !hello|Bot: Hello !|
|fred: !hello bob and bill|Bot: Hello bob!|
|fred: !hello BOB|Bot: Hello BOB!|
|fred: !poke|Bot: fred pokes someone.|
|fred: !poke bob|Bot: fred pokes bob.|
|fred: !poke that guy|Bot: fred pokes that.|
|fred: !hug|Bot: Fred hugs everyone!|
|fred: !hug bob|Bot: Fred hugs bob!|
|fred: !hug everyone and their mother|Bot: Fred hugs everyone and their mother!|
|fred: !gc|Bot: FRED FIRES THE GLITTER CANNON!|
|fred: !gc someone|Bot: FRED FIRES THE GLITTER CANNON!|
|fred: !riot|Bot: ༼ つ ◕\_◕ ༽つ SHOUT OR RIOT ༼ つ ◕\_◕ ༽つ|
|fred: !riot eat cake|Bot: ༼ つ ◕\_◕ ༽つ EAT CAKE OR RIOT ༼ つ ◕\_◕ ༽つ|
|fred: !cookies|Bot throws a cookie at because master fred said so! (Cookies given: 1)|
|fred: !cookies bob|Bot throws a cookie at bob because master fred said so! (Cookies given: 2)|
|fred: !foo bAr|Bot: bar BAR Bar BAr|
|fred: !bar1 this is a TEST sentence.|Bot: This is a test sentence.|
|fred: !bar2 this is a TEST sentence.|Bot: This Is A Test Sentence.|
|fred: !bar3 this is a TEST sentence.|Bot: This is a TEST sentence.|
|fred: !bar4 this is a TEST sentence.|Bot: This Is A TEST Sentence.|
|fred: !words|Bot: words something words  words dunno words &nbsp;words|
|fred: !words one|Bot: words one words one words one words &nbsp;words|
|fred: !words one two|Bot: words one words two words two words two words|
|fred: !words one two three|Bot: words one words two words three words two words|
|fred: !words one two three four|Bot: words one words two words three words four words|
|fred: !multi|Bot: Watch MaddiieManeater, MKtheWorst, and me at the same time! example.com/multistream/the_lone_devil/|
|fred: !multi MaddiieManeater|Bot: Watch MaddiieManeater, MKtheWorst, and me at the same time! example.com/multistream/the_lone_devil/maddiiemaneater/|
|fred: !multi MaddiieManeater MKtheWorst|Bot: Watch MaddiieManeater, MKtheWorst, and me at the same time! example.com/multistream/the_lone_devil/maddiiemaneater/mktheworst/|
|fred: !ifargs|Bot: A secret message appears if you run this command with args.|
|fred: !ifargs some args|Bot: A secret message appears if you run this command with args. I mean, I suppose it's a secret, but it's not a very interesting one :P|
|fred: !ifarg2|Bot: More args are needed. You need 2 args.|
|fred: !ifarg2 one|Bot: More args are needed. You need 2 args.|
|fred: !ifarg2 one two|Bot: More args are not needed. You needed 2 args.|
