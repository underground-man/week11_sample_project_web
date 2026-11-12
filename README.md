22300330 / 박찬

① Source Code Analysis

Project Structure & Spring MVC

PostController, PostService, PostRepository는 각각 어떤 역할을 하며, 서로 어떻게 연결되는가?
본래 controller의 주요 역할은 업로드 및 다운로드 처리이다.
이 프로젝트에서 controller는 프론트에서의 form또한 처리하기위해 ModelAttribute어노테이션으로 postform 데이터를 DTO에 바인딩 시킨다. 

service는 controller에서 받아온 데이터를 파일검증,저장및 다운로드하는것이 역할이다.
이 프로젝트에서는 Transactional 어노테이션을 필요에따라 readonly=true 값 변경하여 뭔데이게 pdf에 없잖아 repository값을 반환 시킨다.

repository는 jpa를 통한 db처리가 역할이다.
jpa를 통하여서 직접 구현하지 않아도 interface만 구현해도 되게 되었다.


게시글 작성 시 HTML Form의 입력값과 첨부파일은 Controller에 어떻게 전달되는가?
게시글 작성시 form은 위에작성했다 시피 ModelAttribute를 통하여서 controller가 전달 받을수 있게 변환하여 전달된다.



JPA & Entity Relationship
게시글 작성시 Post와 Attachment Entity는 어떤 관계이며, @ManyToOne과 @OneToMany는 각각 어떤 역할을 하는가?

post 와 Attachment는 1대 다 관계이다. 그 이유는 게시글(post)에 첨부물 (attachment) 다수가 들어갈수 있기 땜문이다,
어노테이션으로 보면, 
domain의 Attachment.java는 @ManyToOne어노테이션이 있다.
@OneToMany는 post.java에 있다. 
post입장에서, 자신의 attachment들을 복수로 관리할수 있다는것이다.


cascade = CascadeType.ALL과 orphanRemoval = true는 어떤 역할을 하며, 실제 디스크에 저장된 파일에도 영향을 주는가?

cascade = CascadeType.ALL 의 역할은, 부모 endity에 수행한 작업을 자식 entity에도 전달하는기능이다.
orphanRemoval = true는 부모와의 연결이 끊어진 자식 entity를 db에서 삭제 한다.

다만 두 설정 모두다 실제 파일을 삭제 하지 않는다. 


File Upload

MultipartFile은 무엇이며, 업로드된 파일은 어떤 과정을 거쳐 서버에 저장되는가?

MultipartFile은 사용자가 업로드한 파일을 spring이 java에서 다룰수 있도록 표현한 객체이다.

업로드된 파일이 서버에 저장되는 과정은 
사용자 파일선택 -> form 파일 전송 -> multipartFile로 변환 -> controller가 파일 형식기타 검사 -> 
UUID 저장 파일명 생성 -> 폴더에 실제 파일 저장 -> attachments 테이블에 파일정보 저장



파일을 원본 이름 대신 UUID 기반 파일명으로 저장하는 이유는 무엇인가?

파일이름 충돌과 위험한 파일명을 방지하기 위해서이다. 
파일의 이름이 중복되더라도 UUID 기반 파일 저장시 충돌되지 않는다 


File Download & Security

파일 다운로드 시 Resource, ResponseEntity, Content-Disposition은 각각 어떤 역할을 하는가?

resource: 서버에 저장된 파일을 Spring에서 읽고 전달하기 위한 객체이다
ResponseEntity: 브라우저에 보낼 HTTP 응답 전체를 구성합니다.
Content-DisPosition: 브라우저에게 파일을 어떻게 처리해야 하는지 알려주는 HTTP 헤더이다.


파일 저장 과정에서 normalize()와 startsWith()를 사용하는 이유는 무엇인가?

FileStorageService.store()와 FileStorageService.load()에서 
normalize()는 .이나 ..이 포함된 경로를 정리하여 실제 저장·조회 경로를 확인한다.
startsWith(uploadDir)는 정리된 경로가 지정된 업로드 폴더 내부에 있는지 검사한다.
이를 통해 파일이 uploads 폴더 밖에 저장되거나 조회되는 Path Traversal 공격을 방지한다.

hmentController.download(): 파일 조회 및 HTTP 다운로드 응답 처리

Step 5. Testing & Deployment

### Local Testing

 01. Sample Project 초기 실행 화면



![Sample Project 초기 실행 화면](<img/img.png>)

 02. 파일 업로드


![파일 선택 화면](<img/스크린샷 2026-11-12 184828.png>)


![파일 업로드 성공 화면](<img/스크린샷 2026-11-12 184832.png>)


![게시글 목록 반영 화면](<img/스크린샷 2026-11-12 185106.png>)

03. 파일 다운로드



![파일 다운로드 성공 화면](<img/스크린샷 2026-11-12 184846.png>)

04. 이미지 미리보기



![이미지 미리보기 구현 전](<img/스크린샷 2026-11-12 185126.png>)



![이미지 미리보기 실행 화면](<img/스크린샷 2026-11-12 224646.png>)

게시글 CRUD 확인



![게시글 삭제 확인 화면](<img/스크린샷 2026-11-12 184850.png>)



GitHub & Render Deployment

- GitHub Repository: https://github.com/underground-man/week11_sample_project_web
- Render Deployment URL: 
- GitHub Push 후 Auto Deploy: 배포 후 확인 결과 입력
- 