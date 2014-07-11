(ns hash.core
  (:require [clojure.java.io :as io])
  (:import
   (org.apache.commons.compress.compressors.bzip2
    BZip2CompressorInputStream)))

(defn- bz2-reader
  [filename]
  (-> filename io/file io/input-stream BZip2CompressorInputStream. io/reader))

(defn- fits-parameters
  [length characters subject]
  (let [character-set (set characters)]
    (and
     (= length (count subject))
     (clojure.set/subset? (set (sort subject)) character-set))))

(defn- extract-dictionary-items [filename]
  (with-open [reader (bz2-reader filename)]
    (doall
     (filter
      #(fits-parameters 9 letters %)
      (line-seq reader)))))

(def ^{:const true}
  letters "acdegilmnoprstuw")

(def ^{:const true}
  starting-h 7)

(defn calculate
  [h letter]
  (+
   (* h 37)
   (.indexOf
    letters
    (str letter))))

(defn- perform [str]
  (reduce calculate starting-h str))

(defn find-question [answer length]
  (first
   (filter
    #(= (hash.core/perform %) answer)
    (extract-dictionary-items "rockyou.txt.bz2"))))
