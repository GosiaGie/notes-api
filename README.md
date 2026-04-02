# notes-api

notes-api to aplikacja backendowa, oparta na architekturze REST API.
Aplikacja służy do zarządzania notatkami - tworzenia ich, edycji, wyświetlania, a także udostępniania i zarządzania dostępem.

Główne cechy:
1. BEZPIECZEŃSTWO
* wielopoziomowa autoryzacja - chronione endpointy wymagają tokena JWT, a dostęp do zasobu (notatki) jest również weryfikowany pod kątem roli użytkownika,
* do endpointa /login dodano implementację Rate Limitingu (kod 429 Too Many Requests).
2. AUDYT I HISTORIA
* zastosowano Hibernate Envers, co pozwoliło w pełni śledzić historię notatek,
* zaimplementowano własny UserRevisionListener, który wiąże każdą zmianę notatki z konkretnym użytkownikiem.
3. SPOJNOŚĆ DANYCH
* dzięki polu @Version i wykorzystaniu strategii Optimistic Locking dane są chronione przed jednoczesną edycją przez wielu użytkowników,
* wymuszenie zapisu (flush) daje gwarancję, że odpowiedź do klienta będzie zawierała aktualną wersję (przetestowane w testach integracyjnych).
4. ARCHITEKTURA
* architektura wartstwowa, z podziałem na Controller -> Service -> Repository, co pozwoliło na separację infrastruktury HTTP (Controller) od logiki biznesowej (Service) i dostępu do dodanych (Repository),
* DTO (Data Transfer Objects) - z uwagi na bezpieczeństwo (aby nie zwracać do klienta zbyt dużo informacji z bazy danych) zastosowano wzorzec DTO i klasy Request i Response,
* Implementacja UserPrincipal, co pozwala na łatwy dostęp do ID użytkownika.
5. TESTY
* testy jednostkowe - zastosowano framework JUnit oraz Mockito do mockowania zależności,
* testy integracyjne - wykorzystano Testcontainers (MySQL), co gwarantuje identyczne środowisko testowe jak produkcyjne.
  
