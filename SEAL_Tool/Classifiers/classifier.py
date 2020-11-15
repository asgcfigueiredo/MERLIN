import pandas as pd
import os
import pickle 
from os import listdir
import os
from sklearn.neighbors import KNeighborsClassifier

class Classifier:
    def __init__(self):
        currentDir = os.path.dirname(os.path.abspath(__file__))
        self.model = pickle.load(open(currentDir + '\\SVMClassifier', 'rb'))
    
    def process(self, filename):
        fileCsv = pd.read_csv(filename)
        tsX = fileCsv.iloc[:,:]
        predY = self.model.predict(tsX)
        if predY[0] == 1:
            return True
        else:
            return False