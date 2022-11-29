package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.APINotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    private Item initItem(Long id, String name, Double price, String description) {
        Item item = new Item();
        item.setId(id == null ? 1L : id);
        item.setName(name == null ? "Iphone 6s cover" : name);
        item.setPrice(BigDecimal.valueOf(price == null ? 12.90 : price));
        item.setDescription(description == null ? "Clear cover for iphone 6s" : description);
        return item;
    }

    @Test
    public void should_findById() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(initItem(null, null, null, null)));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        Item expected = initItem(null, null, null, null);

        assertEquals(expected, response.getBody());
    }

    @Test(expected = APINotFoundException.class)
    public void shouldNot_findById() {

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        itemController.getItemById(1L);
    }

    @Test
    public void should_findAll() {
        Item item1 = initItem(1L, "item 1", 11.90, "Item 1");
        Item item2 = initItem(2L, "item 2", 12.90, "Item 2");
        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertTrue(!response.getBody().isEmpty());
    }

    @Test(expected = APINotFoundException.class)
    public void shouldNot_findByName() {

        when(itemRepository.findByName("item name")).thenReturn(new ArrayList<>());
        itemController.getItemsByName("item name");
    }

    @Test
    public void should_findByName_listItems() {

        Item item1 = initItem(1L, "item", 11.90, "Item 1");
        Item item2 = initItem(2L, "item", 12.90, "Item 2");
        List<Item> expected = Arrays.asList(item1, item2);

        when(itemRepository.findByName("item")).thenReturn(Arrays.asList(item1, item2));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("item");
        assertNotNull(response);
        assertArrayEquals(response.getBody().toArray(), expected.toArray());
    }

    @Test
    public void should_findByName_singleItem() {

        Item item = initItem(1L, "item", 11.90, "Item Description");
        List<Item> expected = Arrays.asList(item);

        when(itemRepository.findByName("item")).thenReturn(Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("item");
        assertNotNull(response);
        assertArrayEquals(response.getBody().toArray(), expected.toArray());
    }
}
