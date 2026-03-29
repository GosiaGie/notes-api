# notes-api

notes-api to aplikacja backendowa, oparta na architekturze REST API.
Aplikacja służy do zarządzania notatkami - tworzenia ich, edycji, wyświetlania, a także udostępniania i zarządzania dostępem.

Główne cechy:
* wielopoziomowa autoryzacja - chronione endpointy wymagają tokena JWT, a dostęp do zasobu(notatki) jest również weryfikowany pod kątem roli użytkownika,
* automatyczne wersjonowanie wystąpienia encji dzięki Envers, co pozwoliło zabezpieczyć nadpisanie rekordu przez starsze wersje bez implementacji całej logiki ręcznie; w przypadku próby zapisania tej samej wersji notatki system wykryje konflikt i zwróci do klienta kod HTTP 409,
* architektura wartstwowa, z podziałem na Controller -> Service -> Repository, co pozwoliło na separację infrastruktury HTTP (Controller) od logiki biznesowej (Service) i dostępu do dodanych (Repository),
* DTO (Data Transfer Objects) - z uwagi na bezpieczeństwo (aby nie zwracać do klienta zbyt dużo informacji z bazy danych) zastosowano wzorzec DTO i klasy Request i Response,
* Implementacja UserPrincipal, co pozwala na łatwy dostęp do ID użytkownika.
  
