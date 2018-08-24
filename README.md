# mastodonusers

A Clojure app to report and log the current number of users in the fediverse.

This app depends on the API provided by [instances.social](https://instances.social/api/);
I have not investigatged the methodology by which they assemble their list of instances. In
any case it's impossible to tell how many of those accounts represent real individual human
users, and I don't currently do anything to establish how many of those accounts are active.

Please don't use this app to DDoS `instances.social`; it really shouldn't be necessary to run
it more than once a day.

## Configuring

You should obtain a key from [instances.social](https://instances.social/api/token) and write it (just the file, nothing else) into `resources/secret.txt`.

## Building

`lein uberjar`

## Usage

To print the current number of users:

`java -jar mastodonusers-[version]-standalone.jar -v 1`

To log the current users in an EDN file:

`java -jar mastodonusers-[version]-standalone.jar path-to-edn-file`

To log current users in an EDN file and output the file as CSV to the console:

`java -jar mastodonusers-[version]-standalone.jar --csv path-to-edn-file`


## License

Copyright © 2018 Simon Brooke

Distributed under the GNU General Public License either version 2.0 or (at
your option) any later version.
