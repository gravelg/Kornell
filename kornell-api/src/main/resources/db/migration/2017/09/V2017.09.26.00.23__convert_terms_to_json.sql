update Institution set terms = concat('{"termsLanguageItems":[{"language":"pt_BR","terms":"', concat(replace(terms, '"', '\\\\\\\"'), '"},{"language":"en_US","terms":""}]}')) where terms not like '{%'