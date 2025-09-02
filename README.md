# Yoda Content Service

A dedicated microservice for content management in the Yoda CMS ecosystem. This service provides comprehensive APIs for handling all content-related operations including creation, updates, search, and content lifecycle management.

## 🚀 Features

- **Content CRUD Operations**: Create, read, update, and delete content items
- **Advanced Search**: Full-text search with multiple filters and pagination
- **Content Lifecycle Management**: Publish/unpublish, feature/unfeature content
- **Category Management**: Organize content by categories
- **Tag-based Filtering**: Find content by tags
- **Site-specific Content**: Multi-site content management
- **SEO Support**: Meta tags, canonical URLs, and SEO optimization
- **View Tracking**: Hit counter and analytics support
- **RESTful API**: Comprehensive REST endpoints with OpenAPI documentation
- **Caching**: Redis integration for performance optimization
- **Health Monitoring**: Actuator endpoints for monitoring

## 🏗️ Architecture

### Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Database**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.11
- **Cache**: Redis
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Security**: Spring Security
- **Container**: Docker & Docker Compose

### Service Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │◄──►│  Content Service│◄──►│   Database      │
│   (Main Yoda)   │    │   (Port 8081)   │    │   (MySQL)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Cache Layer   │
                       │   (Redis)       │
                       └─────────────────┘
```

## 📋 Prerequisites

- **Java**: JDK 21 (OpenJDK or Eclipse Temurin)
- **Gradle**: 8.0+ (or use Gradle Wrapper)
- **Docker**: 20.10+ (for containerized deployment)
- **Docker Compose**: 2.0+
- **MySQL**: 8.0+
- **Redis**: 6.0+

## 🛠️ Setup & Installation

### 1. Clone and Navigate

```bash
cd yoda-content
```

### 2. Database Setup

The service uses the same MySQL database as the main Yoda application. Ensure the database is running:

```bash
# Start the database services
docker-compose up -d mysql redis
```

### 3. Configuration

The application is pre-configured to work with the Docker Compose setup. Key configurations:

- **Database**: `localhost:3306/yoda`
- **Redis**: `localhost:6379`
- **Service Port**: `8081`

### 4. Build and Run

```bash
# Build the project
./gradlew clean build

# Run the application
./gradlew bootRun
```

The service will be available at: `http://localhost:8081`

## 📚 API Documentation

### Swagger UI

Access the interactive API documentation at:
```
http://localhost:8081/swagger-ui.html
```

### OpenAPI Specification

Download the OpenAPI specification at:
```
http://localhost:8081/api-docs
```

## 🔌 API Endpoints

### Content Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/content` | Create new content |
| `PUT` | `/api/v1/content/{id}` | Update existing content |
| `GET` | `/api/v1/content/{id}` | Get content by ID |
| `GET` | `/api/v1/content/key/{naturalKey}` | Get content by natural key |
| `DELETE` | `/api/v1/content/{id}` | Delete content (soft delete) |

### Content Search & Discovery

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/content/search` | Search contents with filters |
| `GET` | `/api/v1/content/category/{categoryId}` | Get contents by category |
| `GET` | `/api/v1/content/featured` | Get featured contents |
| `GET` | `/api/v1/content/published` | Get published contents |
| `GET` | `/api/v1/content/tags` | Get contents by tags |
| `GET` | `/api/v1/content/site/{siteId}` | Get contents by site |

### Content Lifecycle

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/content/{id}/publish` | Publish content |
| `POST` | `/api/v1/content/{id}/unpublish` | Unpublish content |
| `POST` | `/api/v1/content/{id}/feature` | Feature content |
| `POST` | `/api/v1/content/{id}/unfeature` | Unfeature content |
| `POST` | `/api/v1/content/{id}/view` | Update hit counter |

## 📊 Data Model

### Content Entity

```java
public class Content {
    private Long id;
    private String title;
    private String shortDescription;
    private String description;
    private String pageTitle;
    private String featuredImage;
    private String contentType;
    private String status;
    private String tags;
    private String metaKeywords;
    private String metaDescription;
    private String canonicalUrl;
    private String seoTitle;
    private String seoDescription;
    private String language;
    private Integer sortOrder;
    private Boolean allowComments;
    private Boolean published;
    private Boolean featureData;
    private Long categoryId;
    private Integer siteId;
    private String naturalKey;
    private LocalDateTime publishDate;
    private LocalDateTime expireDate;
    private Integer hitCounter;
    private Integer score;
    // ... audit fields
}
```

## 🔍 Search Capabilities

The content service supports advanced search with the following filters:

- **Keyword Search**: Full-text search across title, description, and short description
- **Content Type**: Filter by content type
- **Status**: Filter by content status
- **Category**: Filter by category ID
- **Site**: Filter by site ID
- **Published Status**: Filter published/unpublished content
- **Featured Status**: Filter featured content
- **Language**: Filter by language
- **Date Ranges**: Filter by publish date or creation date
- **Sorting**: Sort by title, publish date, hit counter, score, or creation date

### Search Example

```json
{
  "keyword": "spring boot",
  "contentType": "article",
  "published": true,
  "categoryId": 1,
  "siteId": 1,
  "language": "en",
  "sortBy": "createDate",
  "sortOrder": "desc",
  "page": 1,
  "size": 20
}
```

## 🚀 Integration with Main Yoda

### Service Discovery

The content service is designed to be called by the main Yoda application. Integration points:

1. **API Gateway**: Route content requests to this service
2. **Load Balancer**: Distribute load across multiple instances
3. **Service Mesh**: For advanced service-to-service communication

### Configuration in Main Yoda

Add the content service URL to your main Yoda application configuration:

```yaml
yoda:
  services:
    content:
      url: http://localhost:8081
      timeout: 5000
```

## 🔧 Development

### Running Tests

```bash
./gradlew test
```

### Building Docker Image

```bash
./gradlew bootBuildImage
```

### Running with Docker Compose

```bash
docker-compose up -d
```

## 📈 Monitoring & Health

### Health Check

```
GET http://localhost:8081/actuator/health
```

### Metrics

```
GET http://localhost:8081/actuator/metrics
```

### Prometheus Metrics

```
GET http://localhost:8081/actuator/prometheus
```

## 🔒 Security

The service includes basic security configuration:

- CSRF protection disabled for API endpoints
- OpenAPI documentation accessible
- Actuator endpoints protected
- Content API endpoints publicly accessible

For production deployment, consider:

- JWT token authentication
- API key authentication
- Rate limiting
- CORS configuration
- HTTPS enforcement

## 🐳 Docker Deployment

### Docker Compose

```yaml
services:
  yoda-content:
    build: .
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql
      - redis
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `local` |
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `3306` |
| `DB_NAME` | Database name | `yoda` |
| `DB_USERNAME` | Database username | `myuser` |
| `DB_PASSWORD` | Database password | `secret` |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is part of the Yoda CMS ecosystem and follows the same licensing terms.

## 🆘 Support

For support and questions:

- Create an issue in the repository
- Contact the development team
- Check the main Yoda documentation

---

**Yoda Content Service** - Empowering content management in the Yoda ecosystem.
