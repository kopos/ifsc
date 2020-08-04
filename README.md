# ifsc

A simple microservice to help validate IFSC codes.

Based on the amazing [IFSC dataset][dataset] provided by [RazorPay][repo], this is a clojure micro-service providing only 3 API endpoints. The API end points closely mimic the Elixir server supplied in the [RazorPay Repo][repo]

IFSC data is stored in an embedded H2 database (ifsc.db) in the repo itself.

Few other data related to branches are loaded from files into atoms.

## Usage

* Compile with `lein uberjar`

* Run with `PORT=<PORT> java -jar target/*-standalone.jar`

* Make curl calls to
  * `http://localhost:<PORT>/api/v1/ifsc/:code `
  * `http://localhost:<PORT>/api/v1/ifsc/:code/validate`
  * `http://localhost:<PORT>/api/v1/banks/:code`

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

[dataset]: https://github.com/razorpay/ifsc
[repo]: https://github.com/razorpay/ifsc/tree/master/src/elixir
