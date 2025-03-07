;; This is Amino Acid ontology created using an Excel spreadsheet as a
;; source of information for the ontology values.
;; Author Aisha Blfgeh Newcastle University
;; Purpose: demonstrating ontology workflow using Excel sheet
;; UKON 2018 at Keele University

;; * Introduction
;; We start the programme by defining the name space

;; #+begin_src clojure
(ns amino.aao
  (:use [tawny.owl]
        [tawny.pattern]
        [tawny.repl]
        [tawny.reasoner]
        [dk.ative.docjure.spreadsheet])
  (:require [clojure.string :as str]
            [tawny.protocol :as p])
  (:import [java.net URLEncoder]))
;; #+end_src

;; #+begin_comment
;; #+begin_src clojure
(defn column-inf
  "Reads column's values and save them in a set to remove redundancy
  SHEET - sheet information.
  COL - column number starting from one."
  [sheet col]
  (case col
    1 (distinct(flatten
       (seq (sorted-set
         (map vals (select-columns {:A :aminoacid} sheet))))))
    2 (distinct(flatten
       (seq (sorted-set
         (map vals (select-columns {:B :short} sheet))))))
    3 (distinct(flatten
       (seq (sorted-set
         (map vals (select-columns {:C :ltr} sheet))))))
    4 (distinct(flatten
       (seq (sorted-set
         (map vals (select-columns {:D :hydrophobicity} sheet))))))
    5 (distinct(flatten
       (seq (sorted-set
         (map vals (select-columns {:E :charge} sheet))))))
    6 (distinct(flatten
       (seq (sorted-set
         (map vals (select-columns {:F :size} sheet))))))
    7 (distinct(flatten
       (seq (sorted-set
         (map vals (select-columns {:G :polarity} sheet))))))
    8 (distinct(flatten
       (seq (sorted-set
             (map vals (select-columns {:H :sidechainstructure} sheet))))))
    9 (distinct(flatten
       (seq (sorted-set
             (map vals (select-columns {:I :mycol} sheet))))))
    ))
;; #+end_src
;; #+end_comment

;; Some Tawny-OWL functions to read and arrange information extracted
;; from the Excel sheet to create classes for the ontology

;; #+begin_src clojure
(defn row-inf
  "Extract and save one row information into one lazy sequence.
  SHEET - is the sheet name.
  ROW -   is row number."
  [sheet row]
  (map #(and %
             (str/replace (str/trim %) #"\s+" "-"))
       (map read-cell
            (cell-seq (take 1 (drop row (row-seq sheet)))))))

(defn create-class
  "Create one class using a name in a string format.
  NAME - name of the class as string."
  [name]
  (intern-owl-string name (owl-class name)))

(defn create-classes
  "Create classes using a sequence of names (strings).
  NAMES - strings sequence of names of the classes."
  [names]
  (doall
   (map create-class names)))

(defn create-sub-classes
  "Define subclasses giving super class.
  SUPER - the super class
  ARGS - subclasses in a list"
  [super & args]
  ;; (tawny.pattern/value-partition super args)
  ;; need to remove super class name before?????
  (as-subclasses super args))
;; #+end_src

;; #+begin_comment
;; save workbook in a variable and sheet1 as well
(def workbook (load-workbook "AminoAcids.xlsx"))
(def sheet (select-sheet "AminoAcids" workbook))
(def test-sheet (select-sheet "TestAminoAcids" workbook))
;; #+end_comment

;; * Amino Acids Ontology
;; Here the ontology and class definitions

;; #+begin_src clojure
(defontology aao
  :comment "This is Amino Acids ontology"
  :iri "http://ncl.ac.uk/aao")
;; #+end_src

;; #+begin_src clojure
(defclass PhysicoChemicalProperty)
;; #+end_src

;; #+begin_comment
(defmacro defaapartition [& body]
  '(tawny.pattern/value-partition
     ~@body :super PhysicoChemicalProperty
     :domain AminoAcid))

;; (def charges-str (column-inf sheet 5))
;; (def sizes-str (column-inf sheet 6))
;; (def polarities-str (column-inf sheet 7))
;; (tawny.pattern/value-partition (first charges-str) (rest charges-str))
;; (tawny.pattern/value-partition (first polarities-str) (rest polarities-str))
;; #+end_comment

;; First row of the sheet used to define the classes using the above functions
;; #+begin_src clojure
(def classes (row-inf sheet 0))
(create-classes classes)
;; #+end_src

;; Then we adjust the super class for the folloing classes

;; #+begin_src clojure
(refine Hydrophobicity
        :super PhysicoChemicalProperty)
(refine Charge
        :super PhysicoChemicalProperty)
(refine Size
        :super PhysicoChemicalProperty)
(refine Polarity
        :super PhysicoChemicalProperty)
(refine SideChainStructure
        :super PhysicoChemicalProperty)
;; #+end_src

;; #+begin_comment
;; define value partitions for the last five columns in the sheet
(doall
 (for [r (range 4 9)]
  (let [x (column-inf sheet r)]
    (tawny.pattern/value-partition (first x) (rest x)))))

;; First and second examples of the spreadsheet rows
;; (def example1 (row-inf sheet 1))
;; (def example11 (row-inf sheet 1))
;; (def example2 (row-inf sheet 2))

;; variables to be used in creating subclasses for each class
;; (def hydrophobicities (create-classes (column-inf sheet 4)))
;; (def charges (create-classes (column-inf sheet 5)))
;; (def sizes (create-classes (column-inf sheet 6)))
;; (def polarities (create-classes (column-inf sheet 7)))
;; (def side-chains (create-classes (column-inf sheet 8)))

;; (tawny.pattern/value-partition Charge (column-inf sheet 5))

;; (create-classes classes)
;; #+end_comment

;; ** Object properties

;; #+begin_src clojure
(defoproperty hasHydrophobicity
  :domain AminoAcid
  :range Hydrophobicity
  :characteristic
  :functional)

(defoproperty hasCharge
  :domain PhysicoChemicalProperty
  :range Charge)
(defoproperty hasPolarity
  :domain PhysicoChemicalProperty
  :range Polarity)
(defoproperty hasSize
  :domain PhysicoChemicalProperty
  :range Size)
(defoproperty hasSideChainStructure
  :domain PhysicoChemicalProperty
  :range SideChainStructure)

;; ** Annotation properties
(defaproperty hasLongName)
(defaproperty hasShortName)
(defaproperty hasSingleLetterName)
;; #+end_src

;; #+begin_comment
;; create subclasses for a giving class using the column info.
;; (create-sub-classes Charge charges)
;; (create-sub-classes Hydrophobicity hydrophobicities)
;; (create-sub-classes Size sizes)
;; (create-sub-classes Polarity polarities)
;; (create-sub-classes SideChainStructure side-chains)

;; (Def ex1 (map intern-owl-string example1
;;      (map owl-class example1)))
;; #+end_comment

;; This is the wrapping function to fill the patterns of the ontology
;; using extracted information

;; #+begin_src clojure
(defn amino-acid
  "Define new amino acid based on the row information for the Excel spreadsheet.
  EXAMPLES - list of the amino acid information as strings extracted
  form the spreadsheeet"
  [examples]
  (let [aa (owl-class (first examples)
                    :super AminoAcid
                    (owl-some hasCharge
                              (entity-for-string aao (nth examples 4)))
                    (owl-some hasHydrophobicity
                              (entity-for-string aao(nth examples 3)))
                    (owl-some hasPolarity
                              (entity-for-string aao (nth examples 6)))
                    (owl-some hasSideChainStructure
                              (entity-for-string aao (nth examples 7)))
                    (owl-some hasSize
                              (entity-for-string aao (nth examples 5)))
                    :label (first examples)
                    :annotation
                    (annotation hasLongName (nth examples 0))
                    (annotation hasShortName (nth examples 1))
                    (annotation hasSingleLetterName (nth examples 2))
                    )
        ]
  (map ->Named
       examples (repeat aa))))

;; all amino acids from the excel sheet
(doall
 (map amino-acid (for [r (range 1 20)]
                     (row-inf sheet r))))
;; #+end_src


;; * Amino Acids table
;; This is the table used as Excel sheet to build Amino acids ontology

;; #+STARTUP: align
;; | Amino Acid    | Short || LTR | Hydrophobicity | Charge   | Size  || Polarity | Side Chain Structure |
;; |---------------+-------++-----+----------------+----------+-------++----------+----------------------|
;; | <l2>          | <l1>  || <l1>| <l2>           | <l2>     | <l2>  || <l2>     | <c2>                 |
;; | Arginine      | Arg   || R   | Hydrophilic    | Positive | Large || Polar    | Aliphatic            |
;; | Asparagine    | Asn   || N   | Hydrophilic    | Neutral  | Small || Polar    | Aliphatic            |
;; | Aspartate     | Asp   || D   | Hydrophilic    | Negative | Small || Polar    | Aliphatic            |
;; | Cysteine      | Cys   || C   | Hydrophobic    | Neutral  | Small || Polar    | Aliphatic            |
;; | Glutamate     | Glu   || E   | Hydrophilic    | Negative | Small || Polar    | Aliphatic            |
;; | Glutamine     | Gln   || Q   | Hydrophilic    | Neutral  | Large || Polar    | Aliphatic            |
;; | Glycine       | Gly   || G   | Hydrophobic    | Neutral  | Tiny  || NonPolar | Aliphatic            |
;; | Histidine     | His   || H   | Hydrophilic    | Positive | Large || Polar    | Aromatic             |
;; | Isoleucine    | Ile   || I   | Hydrophobic    | Neutral  | Large || NonPolar | Aliphatic            |
;; | Leucine       | Leu   || L   | Hydrophobic    | Neutral  | Large || NonPolar | Aliphatic            |
;; | Lysine        | Lys   || K   | Hydrophilic    | Positive | Large || Polar    | Aliphatic            |
;; | Methionine    | Met   || M   | Hydrophobic    | Neutral  | Large || NonPolar | Aliphatic            |
;; | Phenylalanine | Phe   || F   | Hydrophobic    | Neutral  | Large || NonPolar | Aromatic             |
;; | Proline       | Pro   || P   | Hydrophobic    | Neutral  | Small || NonPolar | Aliphatic            |
;; | Serine        | Ser   || S   | Hydrophilic    | Neutral  | Tiny  || Polar    | Aliphatic            |
;; | Theonine      | Thr   || T   | Hydrophilic    | Neutral  | Tiny  || Polar    | Aliphatic            |
;; | Tryptophan    | Trp   || W   | Hydrophobic    | Neutral  | Large || NonPolar | Aromatic             |
;; | Tyrosine      | Try   || Y   | Hydrophobic    | Neutral  | Large || Polar    | Aromatic             |
;; | Valine        | Val   || V   | Hydrophobic    | Neutral  | Small || NonPolar | Aliphatic            |
;; |---------------+-------++-----+----------------+----------+-------++----------+----------------------|
;; #+STARTUP: noalign

;; Last step is to save our ontology as an owl file

;; #+begin_src clojure
(reasoner-factory :hermit)
(save-ontology "aao.owl" :owl)
;; #+end_src

;; Local Variables:
;; lentic-init: lentic-clojure-org-init
;; End:
