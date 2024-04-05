(defproject cm2code "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "https://raw.githubusercontent.com/saidone75/cm2code/main/LICENSE"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main ^:skip-aot cm2code.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
