package fr.library;

public sealed interface SearchCriteria permits ByTitle, ByAuthor, ByYearRange {
}
