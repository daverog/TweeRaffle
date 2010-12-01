package daverog.tweeraffle

import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;

object EntityManagerCreator {
	
	val emfInstance: EntityManagerFactory = Persistence.createEntityManagerFactory("transactions-optional")

    def create = {
    	emfInstance.createEntityManager()
    }
}