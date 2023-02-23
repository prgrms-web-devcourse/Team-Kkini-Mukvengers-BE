package com.prgrms.mukvengers.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.prgrms.mukvengers.domain.store.model.Store;
import com.prgrms.mukvengers.domain.store.repository.StoreRepository;
import com.prgrms.mukvengers.global.config.jpa.JpaConfig;
import com.prgrms.mukvengers.utils.StoreObjectProvider;

@DataJpaTest
@Import(JpaConfig.class)
public abstract class RepositoryTest {

	@Autowired
	protected StoreRepository storeRepository;

	protected Store savedStore;

	@BeforeEach
	void setUp() {
		savedStore = StoreObjectProvider.createStore();
		storeRepository.save(savedStore);
	}
}
