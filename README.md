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



파일을 원본 이름 대신 UUID 기반 파일명으로 저장하는 이유는 무엇인가?
File Download & Security

파일 다운로드 시 Resource, ResponseEntity, Content-Disposition은 각각 어떤 역할을 하는가?
파일 저장 과정에서 normalize()와 startsWith()를 사용하는 이유는 무엇인가?
관련 소스파일 및 메서드명을 함께 명시
각 문항은 2~4문장 정도로 간단히 설명
② Line-by-Line Code Comments

다음 3개 메서드의 핵심 코드에 직접 설명 주석을 작성합니다.

PostController: 게시글 작성 및 파일 업로드 요청 처리
FileStorageService.store(): 파일 검증, 저장 경로 생성 및 실제 파일 저장
AttachmentController.download(): 파일 조회 및 HTTP 다운로드 응답 처리