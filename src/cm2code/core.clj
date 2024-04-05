(ns cm2code.core
  (:gen-class))

(def state (atom {}))
(def src (atom '()))

(swap! state assoc :string "String")
(swap! state assoc :qname "QName")
(swap! state assoc :string-or-qname false)
(swap! state assoc :modifiers "public static final")

(swap! state assoc :type-prefix "TYPE_")
(swap! state assoc :asp-prefix "ASP_")
(swap! state assoc :prop-prefix "PROP_")
(swap! state assoc :localname-suffix "_LOCALNAME")
(swap! state assoc :qname-suffix "_QNAME")
(swap! state assoc :uri-suffix "_URI")
(swap! state assoc :prefix-suffix "_PREFIX")
(swap! state assoc :camelcase-separator "_")

(defn- fix-name [name]
  (let [name (s/replace name #".*:" "")]
    (s/upper-case (s/replace name #"([a-z])([A-Z])" (str "$1" (:camelcase-separator @state) "$2")))))

(defn- create-qname [property-name prefix]
  (gs/format
    "QName.createQName(%s, %s)"
    (str (fix-name (s/replace property-name #":.*$" "")) (:uri-suffix @state))
    (gs/format "%s%s%s" (prefix @state) (fix-name property-name) (:localname-suffix @state))))

(defn- create-string [property-name prefix]
  (gs/format
    "String.format(\"%s:%s\", %s, %s)"
    "%s"
    "%s"
    (str (fix-name (s/replace property-name #":.*$" "")) (:prefix-suffix @state))
    (gs/format "%s%s%s" (prefix @state) (fix-name property-name) (:localname-suffix @state))))

(defn- get-ns-def [namespace]
  (list
    (gs/format "%s %s %s%s = \"%s\";" (:modifiers @state) (:string @state) (s/upper-case (:prefix (:attrs namespace))) (:uri-suffix @state) (:uri (:attrs namespace)))
    (gs/format "%s %s %s%s = \"%s\";" (:modifiers @state) (:string @state) (s/upper-case (:prefix (:attrs namespace))) (:prefix-suffix @state) (:prefix (:attrs namespace)))))

(defn- get-entity-def [entity prefix]
  (if-not (nil? (:attrs entity))
    (list
      (gs/format "%s %s %s%s%s = \"%s\";" (:modifiers @state) (:string @state) (prefix @state) (fix-name (:name (:attrs entity))) (:localname-suffix @state) (s/replace (:name (:attrs entity)) #"^.*:" ""))
      (gs/format "%s %s %s%s%s = %s;" (:modifiers @state) (if (:string-or-qname @state) (:string @state) (:qname @state)) (prefix @state) (fix-name (:name (:attrs entity))) (:qname-suffix @state) ((if (:string-or-qname @state) create-string create-qname) (:name (:attrs entity)) prefix)))))

(defn- get-entities [xml-data type]
  (filter #(not (string? %)) (mapcat :content (filter #(= (name type) (last (s/split (:tag %) #"/"))) (:content xml-data)))))

(defn- gen-src [xml-data]
  (reset! src
          (map str (concat (mapcat #(get-ns-def %) (get-entities xml-data :namespaces))
                           (mapcat #(get-entity-def % :type-prefix) (get-entities xml-data :types))
                           (mapcat #(get-entity-def % :asp-prefix) (get-entities xml-data :aspects))
                           (mapcat #(get-entity-def % :prop-prefix) (mapcat #(get-entities % :properties) (concat (get-entities xml-data :aspects) (get-entities xml-data :types))))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


