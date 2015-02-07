#OTB Project Documentation
###Interacting with a bot in chat

##Version

Version 0.1.2 (WIP - TODO add more examples for new terms)

####Changelog

* 0.1.2
  - Special terms can contain spaces in defaults and other embedded strings
  - More special terms added
* 0.1.1
  - Changed syntax for special terms and defaults
* 0.1.0
  - Described special terms and their modifiers; gave examples of both

##Commands

##Special Terms

There are some special terms which can be used when creating a command to create a response which varies depending on how the command is run. Each special term is replaced by some value, as described in the following table. Some examples of special term usage are shown at the end of the section.

####Terms

| Term | Description |
|:-----|:------------|
|`[[user]]`|The name of the user who ran the command.|
|`[[args]]` or `[[args{{default}}]]`|All arguments with which the command was run (separated by spaces). If the command is run without arguments, and a default is given, the default is used. The default can contain another special term. If no default is given, it is replaced by an empty string (it is left blank).|
|`[[argN]]` or `[[argN{{default}}]]`|The Nth argument, where N is a number greater than 0. If the command is run with fewer than N arguments, and a default is given, the default is used. The default can contain another special term. If no default is given, it is replaced by an empty string (it is left blank).|
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

The following are terms which are significantly more useful when given embedded strings, or not useful at all without them.

| Term | Description |
|:-----|:------------|
|`[[ifargs{{string}}]]`|Prints the specified string only if there is at least one argument. If no string is supplied, it does nothing.|
|`[[ifargN{{string}}]]`|Prints the specified string only if at least N arguments were given. If no string is supplied, it does nothing.|
|`[[foreach{{prepend}}{{append}}]]`|For each argument provided, prints the string specified as ‘prepend’, the argument, and then the string specified as ‘append’. If a modifier (described later) is used, it is applied to each argument (but not to ‘prepend’ or ‘append’). If only one string is provided, it is ‘prepend’. If both are left out, the arguments are printed unmodified (and without spaces).|

####Examples

Here are some examples of special terms and modifiers:

######Commands

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
|!multi|Watch [[foreach.first_cap{{}}{{, }}]] and me at the same time! example.com/multistream/[[channel]]/[[foreach{{}}{{/}}]]|

######Running the commands in chat

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
