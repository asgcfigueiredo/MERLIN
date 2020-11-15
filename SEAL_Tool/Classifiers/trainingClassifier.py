import pandas as pd
import pickle 
import os
from sklearn.model_selection import train_test_split
from sklearn.model_selection import cross_validate
from sklearn.model_selection import KFold
from sklearn.model_selection import cross_val_score
from numpy import mean
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.naive_bayes import GaussianNB 
from sklearn.linear_model import LogisticRegression
from sklearn.neural_network import MLPClassifier
from sklearn.metrics import precision_recall_fscore_support
from sklearn.metrics import accuracy_score
from sklearn.metrics import confusion_matrix
from sklearn.svm import SVC
from imblearn.over_sampling import SMOTE

def print_results(results):
    print("Precision: " + str(results[0]))
    print("Recall: " + str(results[1]))
    print("FScore: " + str(results[2]) + "\n\n")

def print_KCV(score):
    meanRecall = mean(score['test_recall'])
    meanPrecision = mean(score['test_precision'])
    print("Mean accuracy: " + str(mean(score['test_accuracy'])))
    print("Mean precision: " + str(meanPrecision))
    print("Mean recall: " + str(meanRecall))
    fscore = (2 * meanPrecision * meanRecall) / (meanPrecision + meanRecall)
    print("Mean fscore: " + str(fscore))

currentDir = os.path.dirname(os.path.abspath(__file__))
filename = currentDir + "\\training_samples\\combined_csv_all.csv"
fileCsv = pd.read_csv(filename)
#sm = SMOTE(sampling_strategy='minority', random_state=7)

# Fit the model to generate the data.
#oversampled_trainX, oversampled_trainY = sm.fit_sample(fileCsv.drop('Vulnerability', axis=1), fileCsv['Vulnerability'])
#fileCsv = pd.concat([pd.DataFrame(oversampled_trainY), pd.DataFrame(oversampled_trainX)], axis=1)
vulnX = fileCsv.loc[fileCsv['Vulnerability'] == 1]
#noVulnX = fileCsv.loc[fileCsv['Vulnerability'] == 0].sample(n=14497)
noVulnX = fileCsv.loc[fileCsv['Vulnerability'] == 0]
#fileCsv = pd.concat([vulnX, noVulnX])
print("Length vulnerability: " + str(len(vulnX)))
print("Length no vulnerability: " + str(len(noVulnX)))
y = fileCsv['Vulnerability']

fileCsv.pop('Vulnerability')
X = fileCsv.iloc[:,:]
trX, tsX, trY, tsY = train_test_split(X, y, train_size=0.7, stratify = y)

scoring = ['accuracy', 'recall', 'precision']
cv = KFold(n_splits=10, random_state=1, shuffle=True)

#------------Graphical Classifiers------------#

"""-------- CART Classifier ---- """
cart_Dt = DecisionTreeClassifier()

cart_scores = cross_validate(cart_Dt, X, y, scoring=scoring, cv=cv)
cart_Pickle = open(currentDir + '\\Cart-DecisionTreeClassifier', 'wb')

modelCart = cart_Dt.fit(trX, trY)
pickle.dump(modelCart, cart_Pickle)
predY = modelCart.predict(tsX)
results = precision_recall_fscore_support(tsY, predY, average='macro')

print("CART Results: ")
print_KCV(cart_scores)
print("Accuracy: " + str(accuracy_score(tsY, predY)))
print_results(results)


"""-------- Random Forest Classifier ---- """
randomForest = RandomForestClassifier()

rf_scores = cross_validate(randomForest, X, y, scoring=scoring, cv=cv)

randomForest_Pickle = open(currentDir + '\\randomForestClassifier', 'wb')
modelRandomForest = randomForest.fit(trX, trY)
pickle.dump(modelRandomForest, randomForest_Pickle)

predY = modelRandomForest.predict(tsX)
results = precision_recall_fscore_support(tsY, predY, average='macro')
print("Random Forest Results: ")
print_KCV(rf_scores)
print("Accuracy: " + str(accuracy_score(tsY, predY)))
print_results(results)

#------------Probabilistic Classifiers------------#
"""-------- Naive Bayes Classifier ---- """
nB = GaussianNB()

nb_scores = cross_validate(nB, X, y, scoring=scoring, cv=cv)

nb_Pickle = open(currentDir + '\\naiveBayesClassifier', 'wb')
modelNb = nB.fit(trX, trY)
pickle.dump(modelNb, nb_Pickle)

predY = modelNb.predict(tsX)
results = precision_recall_fscore_support(tsY, predY, average='macro')
print("Naive Bayes Results: ")
print_KCV(nb_scores)
print("Accuracy: " + str(accuracy_score(tsY, predY)))
print_results(results)

"""-------- KNN Classifier ---- """
knn = KNeighborsClassifier(n_neighbors = 3)

knn_scores = cross_validate(knn, X, y, scoring=scoring, cv=cv)

knnPickle = open(currentDir + '\\knnClassifier', 'wb')
modelKnn = knn.fit(trX, trY)
pickle.dump(modelKnn, knnPickle)

predY = modelKnn.predict(tsX)
results = precision_recall_fscore_support(tsY, predY, average='macro')
print("KNN Results: ")
print_KCV(knn_scores)
print("Accuracy: " + str(accuracy_score(tsY, predY)))
tn, fp, fn, tp = confusion_matrix(tsY, predY).ravel()
print("TN: " + str(tn))
print("FP: " + str(fp))
print("FN: " + str(fn))
print("TP: " + str(tp))
print_results(results)

"""-------- Logistic Regression Classifier ---- """
lr = LogisticRegression()

lr_scores = cross_validate(lr, X, y, scoring=scoring, cv=cv)

lr_Pickle = open(currentDir + '\\LRClassifier', 'wb')
modelLr = lr.fit(trX, trY)
pickle.dump(modelLr, lr_Pickle)

predY = modelLr.predict(tsX)
results = precision_recall_fscore_support(tsY, predY, average='macro')
print("Logistic Regression Results: ")
print_KCV(lr_scores)
print("Accuracy: " + str(accuracy_score(tsY, predY)))
'''tn, fp, fn, tp = confusion_matrix(tsY, predY).ravel()
print("TN: " + str(tn))
print("FP: " + str(fp))
print("FN: " + str(fn))
print("TP: " + str(tp))'''
print_results(results)

#------------Neural Network Classifiers------------#
"""-------- Multi-Layer Perceptron Classifier ---- """
mlp = MLPClassifier()

mlp_scores = cross_validate(mlp, X, y, scoring=scoring, cv=cv)

mlp_Pickle = open(currentDir + '\\MLPClassifier', 'wb')
modelMlp = mlp.fit(trX, trY)
pickle.dump(modelMlp, mlp_Pickle)

predY = modelMlp.predict(tsX)
results = precision_recall_fscore_support(tsY, predY, average='macro')
print("Multi-Layer Perceptron Results: ")
print_KCV(mlp_scores)
print("Accuracy: " + str(accuracy_score(tsY, predY)))
print_results(results)

"""-------- SVM Perceptron Classifier ---- """
svm = SVC()

svm_scores = cross_validate(svm, X, y, scoring=scoring, cv=cv)

svm_Pickle = open(currentDir + '\\SVMClassifier', 'wb')
modelSvm = svm.fit(trX, trY)
pickle.dump(modelSvm, svm_Pickle)

predY = modelSvm.predict(tsX)
results = precision_recall_fscore_support(tsY, predY, average='macro')
print("SVM Results: ")
print_KCV(svm_scores)
print("Accuracy: " + str(accuracy_score(tsY, predY)))
print_results(results)