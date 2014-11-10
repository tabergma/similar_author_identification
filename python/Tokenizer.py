from sklearn.feature_extraction.text import CountVectorizer

vectorizer = CountVectorizer()

def get_word_frequency(content):
	X = vectorizer.fit_transform(content).toarray()[0]
	names = vectorizer.get_feature_names()

	word_freq = dict()
	for i in range(0, len(X)):
		word_freq[names[i]] = X[i]

	return word_freq
