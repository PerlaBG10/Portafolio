# ParqLink Backend Information
    ParqLink Backend uses an API developed in SpringBoot and a MySQL Database. 
    It also has various functionalities to filter and sort through various parkings through Java codes based on Distance, Price, etc.

# Techonlogies and Important Libraries used:
    SpringBoot
    JWT- Authentication and Security
    Java 21
    Spring Data JPA
    Lombok
    MySQL
    Caffeine
    
## Features:
    JWT-based user authentication (Login/Register)  
    NFC-powered parking session start/end
    Real-time tracking of parking duration and cost
    Filter and sort parkings by distance, price, and name/address
    User session history
    Distance-based calculations via GPS
    Caffeine-Cache Management

## Development Members:
    Samir Shyamdasani Sadarangani

## API Endpoints Overview

    Authentication

    | Method | Endpoint              | Description                   | Auth Required  |
    |--------|-----------------------|-------------------------------|----------------|
    | POST   | `/api/auth/register`  | Register a new user           | No             |
    | POST   | `/api/auth/login`     | Login and receive JWT token   | No             |
    
    ---

    Parking
    
    | Method | Endpoint                  | Description                                          | Auth Required  |
    |--------|---------------------------|------------------------------------------------------|----------------|
    | GET    | `/api/parking/all`        | Get all registered parkings (with filters/sorting)   | Yes            |

    **Optional filters for Parking:
        Latitude
        Longitude
        Max Distance
        Max Price
        Sort (Distance or Price)
        Order
        Name
        Address
    
    ---

    Parking Sessions
    
    | Method | Endpoint                          | Description                                              | Auth Required  |
    |--------|-----------------------------------|----------------------------------------------------------|----------------|
    | POST   | `/api/parking/scan`               | Start or End a session by scanning an NFC tag            | Yes            |
    | GET    | `/api/parking/sessions`           | Get user's past parking sessions with cost and duration  | Yes            |

    
