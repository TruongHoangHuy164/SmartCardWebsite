package com.quizletclone.flashcard.service;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeckServiceCreateTest {
    @Test
    void taoThe_Fail_RepositoryThrowsException() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> deckService.saveDeck(deck));
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_Fail_ThieuUser() {
        Deck deck = new Deck();
        deck.setTitle("No User");
        deck.setUser(null);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getUser());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_Fail_ThieuTieuDe() {
        Deck deck = new Deck();
        deck.setTitle("huy");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getTitle());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_Fail_ThieuNgayTao() {
        Deck deck = new Deck();
        deck.setTitle("No Date");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(null);
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getCreatedAt());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_Fail_TieuDeQuaDai() {
        String longTitle = "1234567890123456789012345678901234567890"; // >30 chars
        Deck deck = new Deck();
        deck.setTitle(longTitle);
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertEquals(longTitle, saved.getTitle());
        assertTrue(saved.getTitle().length() > 30);
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_Fail_RepositoryTraVeNull() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenReturn(null);

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved);
        verify(deckRepository).save(deck);
    }
    @Test
    void taoThe_ThieuUser() {
        Deck deck = new Deck();
        deck.setTitle("No User");
        deck.setUser(null);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getUser());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_ThieuNgayTao() {
        Deck deck = new Deck();
        deck.setTitle("No Date");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(null);
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getCreatedAt());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_TieuDeQuaDai() {
        String longTitle = "1234567890123456789012345678901234567890"; // >30 chars
        Deck deck = new Deck();
        deck.setTitle(longTitle);
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertEquals(longTitle, saved.getTitle());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_MoTaRong() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        deck.setDescription("");
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertEquals("", saved.getDescription());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_MoTaNull() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        deck.setDescription(null);
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getDescription());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_ChuDeNull() {
        Deck deck = new Deck();
        deck.setTitle("    ");
        deck.setUser(null);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        deck.setSubject(null);
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getSubject());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_KhongCongKhai() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        deck.setUser(user);
        deck.setIsPublic(false);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertFalse(saved.getIsPublic());
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_RepositoryTraVeNull() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenReturn(null);

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved);
        verify(deckRepository).save(deck);
    }

    @Test
    void taoThe_RepositoryTraVeDeckCoId() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        Deck deckWithId = new Deck();
        deckWithId.setId(99);
        deckWithId.setTitle("Test");
        deckWithId.setUser(user);
        deckWithId.setIsPublic(true);
        deckWithId.setCreatedAt(deck.getCreatedAt());
        when(deckRepository.save(any(Deck.class))).thenReturn(deckWithId);

        Deck saved = deckService.saveDeck(deck);
        assertNotNull(saved);
        assertEquals(99, saved.getId());
        verify(deckRepository).save(deck);
    }
    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private DeckService deckService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
    }

    @Test
    void taoThe_ThanhCong() {
        Deck deck = new Deck();
        deck.setTitle("Valid Title");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNotNull(saved);
        assertEquals("Valid Title", saved.getTitle());
        assertEquals(user, saved.getUser());
        assertTrue(saved.getIsPublic());
        verify(deckRepository).save(deck);
            System.out.println("createDeck_Success: PASSED");
    }

    @Test
    void taoThe_TieuDeNull() {
        Deck deck = new Deck();
        deck.setTitle("huy");
        deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getTitle());
        verify(deckRepository).save(deck);
            System.out.println("createDeck_NullTitle: PASSED");
    }

    @Test
    void taoThe_CongKhaiNull() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        deck.setUser(user);
        deck.setIsPublic(null);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deck saved = deckService.saveDeck(deck);
        assertNull(saved.getIsPublic());
        verify(deckRepository).save(deck);
            System.out.println("createDeck_NullIsPublic: PASSED");
    }

    @Test
    void taoThe_RepositoryThrowException() {
        Deck deck = new Deck();
        deck.setTitle("Test");
        // deck.setUser(user);
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        when(deckRepository.save(any(Deck.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> deckService.saveDeck(deck));
        verify(deckRepository).save(deck);
            System.out.println("createDeck_RepositoryThrowsException: PASSED");
    }
}
