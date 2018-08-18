# mastodonusers

A Clojure app to report and log the current number of users in the fediverse.

## Configuring

You should obtain a key from [instances.social](https://instances.social/api/token) and write it (just the file, nothing else) into `resources/secret.txt`.

## Building

`lein uberjar`

## Usage

To print the current number of users:

`java -jar mastodonusers-[version]-standalone.jar`

To log the current users in an EDN file:

`java -jar mastodonusers-[version]-standalone.jar path-to-edn-file`

## License

Copyright © 2018 Simon Brooke

Distributed under the GNU General Public License either version 2.0 or (at
your option) any later version.
