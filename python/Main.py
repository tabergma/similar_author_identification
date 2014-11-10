import Read
from Tokenizer import get_word_frequency
from sklearn import svm


def main():
	X = [[0, 0], [1, 1]]
	y = [0, 1]

	clf = svm.SVC()
	clf.fit(X, y)

	content = ["This is a n mn i test"]

	print get_word_frequency(content)


if __name__ == "__main__":
	main()