This is the official repo for the dogerain, much wow project that uses a full stack (Clojure + ClojureScript) project using the [Boot](http://boot-clj.com/) build tool. With `boot run`, you can instantly see the project at [http://localhost:3000/](http://localhost:3000/) and any edits to the ClojureScript will be automatically pushed to the browser. With `boot build`, you can make a standalone JAR file that includes your entire client and server code.

*Note*
You have to have h2 already installed, and have created a database called dogerain using the h2 console. Then make sure to create a file called `db-info.edn` in the project directory. It should look like this:

```clojure
{:dbtype "h2" :dbname "~/dogerain" :user "*username*" :password "*password*"}
```

Then all of the build tools should work!
## Build Instructions

* Install the latest JDK
* Install [Boot](http://boot-clj.com/)
* Develop with `boot run`
* Build JAR file with `boot build`
* Test with `boot test-code`

## Contents

* `resources` The assets
* `src/clj` The server-side code
* `src/cljc` The client and server agnostic code
* `src/cljs` The client-side code

## Connecting to the REPL
After starting the development environment with `boot run`, open up a new terminal and run `boot repl -c`.

This will only launch the **repl client**.
Once it has loaded you need to connect to the **cljs-repl** with `(start-repl)`. Now go to your browser and open up **http://localhost:3000**.
Output similar to this should appear:
```

boot.user=> (start-repl)
<< started Weasel server on ws://127.0.0.1:41101 >>
<< waiting for client to connect ... Connection is ws://localhost:41101
Writing boot_cljs_repl.cljs...
 connected! >>
To quit, type: :cljs/quit
nil
cljs.user=>

```
To test the **REPL** type `(js/alert "Hello")`, which should open an alert box in your browser.

### Connecting with other tools
If you would like to connect with other tools, like your editor, you can do so via Port **9009**.
