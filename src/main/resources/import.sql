INSERT INTO member (id, name, created_at, refresh_token, refresh_token_expires_at) VALUES (999, '테스트 유저', '2023-02-23 08:45:20', '8a0adc35432045c7bc73b4b56ecb9460', '2024-02-23 08:45:20');
INSERT INTO member_provider (id, provider, provider_id, refresh_token, member_id) VALUES (999, 'KAKAO', '4674574152', 'kq5ygn8-sc5v2d-achiFeDldUEeyQAl9Pg9FonJOCj1zTgAAAYaGcAGF', 999);

INSERT INTO tag (tag_id, type, tag_name) VALUES (1, 0, '개인 공부'), (2, 0, '노트북'), (3, 0, '비대면 회의'), (4, 0, '대면 회의'), (1, 1, '카페'), (2, 1, '스터디 룸'), (3, 1, '스터디 카페'), (4, 1, '회의실'), (5, 1, '공유 오피스'), (6, 1, '도서관'), (7, 1, '북카페'), (8, 1, '라운지'), (1, 2, '강남역'), (2, 2, '선릉역'), (3, 2, '삼성역'), (4, 2, '역삼역'), (5, 2, '논현역'), (1, 3, '1인'), (2, 3, '2인 ~ 4인'), (3, 3, '5인 ~ 9인'), (4, 3, '10인 이상'), (5, 3, '기타');
INSERT INTO workspace (name, profile_img, address, latitude, longitude) VALUES ('THE SPACE 몰입 강남점', 'https://pcmap.place.naver.com/restaurant/1597463093/home?from=map&fromPanelNum=1&ts=1677130727486#', '서울 서초구 서운로 164', 37.4981263, 127.0253284);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (1, 0, 13);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (1, 1, 1), (1, 1, 3);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (1, 2, 6), (1, 2, 7), (1, 2, 8);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (1, 3, 19), (1, 3, 20), (1, 3, 21), (1, 3, 22);

INSERT INTO workspace (name, profile_img, address, latitude, longitude) VALUES ('스카이캐슬 스터디카페 선릉 1호점', 'https://pup-review-phinf.pstatic.net/MjAyMjEyMTVfOTgg/MDAxNjcxMDg1NjM0Njk0.acU9aD0p4APxLKC2X6FCRI9oolFdEFfiPvij1VSEBTkg.lFflC0Q5ISTT6lFyRat8DES3W_56IplFZ8I4_5E0jBsg.JPEG/5439730B-765A-456F-99A6-07D2F4A640F0.jpeg',  '서울 강남구 역삼로63길 5', 37.5015508, 127.0523596);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (2, 0, 14);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (2, 1, 1), (2, 1, 2);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (2, 2, 6), (2, 2, 8);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (2, 3, 19), (2, 3, 20), (2, 3, 22);

INSERT INTO workspace (name, profile_img, address, latitude, longitude) VALUES ('토즈 삼성점', 'https://ldb-phinf.pstatic.net/20150901_243/1441037394863qW5Ka_JPEG/13442313_1.jpg', '서울 강남구 테헤란로103길 14 대한기독교서회빌딩 3층', 37.5103708, 127.0641384);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES(3, 0, 15);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (3, 1, 1);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (3, 2, 6);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (3, 3, 19), (3, 3, 21);

INSERT INTO workspace (name, profile_img, address, latitude, longitude) VALUES ('맨하탄 비즈앤스터디카페 역삼점', 'https://ldb-phinf.pstatic.net/20210421_276/1618931653385TtFiE_JPEG/3ZLg3vv5B5Y_L2l9Prb_cDiD.jpg', '서울 강남구 강남대로94길 11 맨하탄어학원빌딩 4층', 37.4995551, 127.0282702);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (4, 0, 16);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (4, 1, 3), (4, 1, 4);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (4, 2, 6), (4, 2, 7);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (4, 3, 19), (4, 3, 20), (4, 3, 21), (4, 3, 22);


INSERT INTO workspace (name, profile_img, address, latitude, longitude) VALUES ('원더시티 뉴욕컵케이크 논현점', 'https://ldb-phinf.pstatic.net/20220819_110/1660910103518lrrFK_JPEG/FA4EE0C2-7559-4669-A7BE-F31C5E17749F.JPG', '서울 강남구 강남대로 556 이투데이 빌딩 1층', 37.5116824, 127.0214521);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (5, 0, 17);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (5, 1, 1), (5, 1, 2);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (5, 2, 6), (5, 2, 9);
INSERT INTO workspace_tag (workspace_id, type, tag_id) VALUES (5, 3, 19), (5, 3, 20);
