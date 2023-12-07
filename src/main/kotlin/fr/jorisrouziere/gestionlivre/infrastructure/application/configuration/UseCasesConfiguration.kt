package fr.jorisrouziere.gestionlivre.infrastructure.application.configuration

import fr.jorisrouziere.gestionlivre.domain.usecase.BookUseCases
import fr.jorisrouziere.gestionlivre.infrastructure.driven.dao.BookDAO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {
    @Bean
    fun bookUseCases(bookDAO: BookDAO): BookUseCases {
        return BookUseCases(bookDAO)
    }
}