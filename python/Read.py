import random

RANDOM_SEED = 0

DATA_FOLDER = "../data/"

DATASET_1 = ["saschalobo/", 15]
DATASET_0 = ["schminktante/", 15]

DATASET_PATH = 0
DATASET_COUNT = 1

TRAINING_TYPE = 1
TEST_TYPE = 2

DATASETS = [DATASET_0, DATASET_1]

SPLIT = 0.67

preparedDataSets = []

def readTrainingFile(dataset, file_number):
	return readFile(dataset, file_number, TRAINING_TYPE)

def readTestFile(dataset, file_number):
	return readFile(dataset, file_number, TEST_TYPE)

def readFile(dataset, file_number, set_type):
	if (preparedDataSets == []):
		print "datasets must be loaded first"
		return

	if (dataset < 0 or dataset >= len(preparedDataSets)):
		print "dataset %d is not available" % dataset
		return

	if (file_number < 0 or file_number > len(preparedDataSets[dataset][set_type])):
		print "dataset %d, file %d is not available" % (dataset, file_number)
		return

	f = file(preparedDataSets[dataset][DATASET_PATH] + str(preparedDataSets[dataset][set_type][file_number]))
	content = f.read()
	f.close()

	return content

def loadDatasets():
	global preparedDataSets
	random.seed(RANDOM_SEED)
	for d in DATASETS:
		path = DATA_FOLDER + d[DATASET_PATH]

		files = range(0, d[DATASET_COUNT])
		random.shuffle(files)

		split_at = int(SPLIT * d[DATASET_COUNT])

		train_set = files[:split_at]
		test_set = files[split_at:]

		preparedDataSets += [path, train_set, test_set],

def main():
	loadDatasets()
	print readTrainingFile(0, 0)
	print readTestFile(0, 0)

if __name__ == "__main__":
	main()