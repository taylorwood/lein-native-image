# Example Projects

Some sample Leiningen projects that have been tested with `lein-native-image` to produce working GraalVM native images.

Run `lein native-image` from an example project directory and execute the generated image.

## jdnsmith

A CLI tool that reads JSON from stdin then writes it to stdout in EDN format.

```
➜ echo "{\"foo\": [{\"bar\": 1.33}]}" | ./target/jdn
{:foo [{:bar 1.33}]}
```

## http-api

A simple web server using http-kit + Ring + Compojure. Build the native image, then start it:
```
➜ ./target/server
Hello, Web!
```

You can now make requests to endpoints that in turn make HTTP requests to other sites and...
- return the response in Hiccup/EDN format:
   ```
   ➜ curl localhost:3000/hick/clojuredocs.org
   ("<!DOCTYPE html>" [:html {} [:head {} [:meta {:content "width=device-width, maximum-scale=1.0", :name "viewport"}] [:meta {:content "yes", :name "apple-mobile-web-app-capable"}] [:meta {:content "default", :name "apple-mobile-web-app-status-bar-style"}] [:meta {:content "ClojureDocs" ...
   ```
- compute the frequency of each character in the response:
   ```
   ➜ curl localhost:3000/freq/clojuredocs.org
   {"frequencies":{" ":1320,"!":11,"A":15,"a":2440,"\"":1392,"B":12,"b":295,"#":66,"C":50,"c":1160,"D":44,"d":574,"❤":1,"%":12,"E":26,"e":1330,"&":144,"F":28,"f":460,"❦":1,"'":44,"G":47,"g":422,"(":44,"H":7,"h":512,")":44,"I":6,"i":1000,"\n":55,"*":3,"J":6,"j":134,"+":2,"K":5,"k":156,",":46,"L":17,"l":1045,"-":345,"M":7,"m":582,".":342,"N":12,"n":981,"/":1156,"O":8,"o":1022,"0":168,"P":48,"p":753,"1":158,"Q":3,"q":15,"2":277,"R":11,"r":1292,"3":228,"S":7,"s":1674,"4":198,"T":13,"t":1431,"5":112,"U":4,"u":542,"6":125,"V":3,"v":526,"7":135,"W":2,"w":263,"8":141,"X":2,"x":42,"9":118,"Y":3,"y":233,"∙":6,":":224,"Z":2,"z":20,";":183,"[":13,"{":11,"<":1012,"|":7,"=":924,"]":13,"}":11,">":1015,"^":1,"?":94,"_":15},"timestamp":"2018-05-27T21:15:52.438Z"}%
   ```

The project's `core.clj` also demonstrates a workaround for dealing with `native-image` limitations related to runtime reflection.

## nlp

A CLI tool to do sentiment analysis on text inputs. Pipe some text into it and get a sentiment index.
```
➜ echo "Oh wow, I can't believe how great this is." | ./nlp
4.0
➜ echo "This is the worst thing I've ever seen." | ./nlp
0.0
```
