OCR Recognition
===============
Undirected Graphical Model for the optical character word recognition task

Author
------
Rishi Dua <http://github.com/rishirdua>


Disclaimer
-----------
The problem below has been borrowed (with minor changes) from the Probabilistic Graphical Models course offered by Dr. Parag Singla at IIT Delhi (Fall 2013 Semester).


Problem Statement
------------------
Implementing and experimenting with a undirected graphical model for the optical character word recognition task. We will be studying computer vision task of recognizing words from images. We can recognize a word by recognizing the individual characters of the word. However recognizing a character a difficult task and each character is recognized independent of its neighbors, which often can result words that are not there in English language. So in this problem we will augment a simple OCR model with additional factors that capture some of our intuitions based on character co-occurrences and image similarities.

The undirected graphical model for recognition of a given word consists of two types of variables:
- Image Variables: These are observed images that we need to predict the corresponding character of, and the number of these image variables for a word is the number of characters in the word. The value of these image variables is an observed image, represented by an integer id (less than 1000). For the description of the model, assume the id of the image at position i is represented by img(i).
- Character Variables: These are unobserved variables that represent the character prediction for each of the images, and there is one of these for each of the image variables. For our dataset, the domain of these variables is restricted to the ten most frequent characters in the English language (e,t,a,o,i,n,s,h,r,d), instead of the complete alphabet. For the discussion below, assume the predicted character at position i is represented by char(i).


Undirected Graphical Model
--------------------------
The model for a word w will consist of len(w) observed image ids, and the same number of unobserved character variables. For a given assignment to these character variables, the model score (i.e. the probability of the assignment according to the model) will be specified using following factors:

- OCR Factors, $\phi _0$  : These factors capture the predictions of a character-based OCR system, and hence exist between every image variable and its corresponding character variable. The number of these factors of word w is len(w). The value of factor between an image variable and the character variable at position i is dependent on img(i) and char(i), and is stored in ocr.dat file described in the data section.
- Transition Factors, $\phi _t$ : Since we also want to represent the co-occurrence frequencies of the characters in our model, we add these factors between all consecutive character variables. The number of these factors of word w is len(w) − 1. The value of factor between two character variables at positions i and i + 1 is dependent on char(i) and char(i + 1), and is high if char(i + 1) is frequently preceded by char(i) in English words. These values are given to you in trans.dat file described in the data section.
- Skip Factors,  $\phi _s$ : Another intuition that we would like to capture in our model is that similar images in a word always represent the same character. Thus our model score should be higher if it predicts the same characters for similar images. These factors exist between every pair of image variables that have the same id, i.e. this factor exist between all i,j, i = j such that img(i) == img(j). The value of this factor depends on char(i) and char(j), and is 5.0 if char(i) == char(j), and 1.0 otherwise.
- Pair-Skip Factors, $\phi _D$ : These factors exist between pairs of image variables that belong to different words and have the same id. The value of this factor depends on the characters, i.e. its value is 5.0 if the characters are the same, and 1.0 otherwise.


Dataset Format
--------------

Potential Directory:
– ocr.dat: Contains the output predictions of a pre-existing OCR system for the set of thousand images. Each row contains three tab separated values "id a prob" and represents the OCR system’s probability that image id represents character a (P (char = a|img = id) = prob). Use these values directly as the value of the factor between image and character variables at position i, ψo (image(i) = id, char(i) = a) = prob. Since there are 10 characters and 1000 images, the total number of rows in this file is 10,000.
– trans.dat: Stores the factor potentials for the transition factors. Each row contains three tab-separated values ”a b value” that represents the value of factor when the previous character is "a" and the next character is "b", i.e. ψt (char(i) = a, char(i + 1) = b) = value. The number of
rows in the file is 100 (10*10).

Data Directory:
– data/truth-loops.dat: Contains observed images of one word on each row with a empty line between pairs. The observed images for a word are represented by a sequence of tab-separated integer ids (”id1 id2 id3”).
– data/data-loops.dat: Stores the true words for the observed set of images in the respective rows with a empty line between pairs. True words are simply represented as strings (e.g. ”eat”). You will need to iterate through both the files together to ensure you have the true word along with the observed images.


Contribute
----------
- Issue Tracker: https://github.com/rishirdua/ocr-recognition/
- Source Code: https://github.com/rishirdua/ocr-recognition/
- Project page: http://rishirdua.github.io/ocr-recognition/


License
-------
This project is licensed under the terms of the MIT license. See LICENCE.txt for details