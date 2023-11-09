create table airplaneDepart(
    flightName VARCHAR2(50),
    depDay date,
    depTime timestamp,               
    fullDeparture VARCHAR2(200),
    depCode varchar2(50),
    depName varchar2(200),
    fullArrival varchar2(200),
    arrCode varchar2(50),
    arrName varchar2(200)
);

create table airplaneArrive(
    flightName VARCHAR2(50),
    arrDay date,
    arrTime timestamp,               
    fullDeparture VARCHAR2(200),
    depCode varchar2(50),
    depName varchar2(200),
    fullArrival varchar2(200),
    arrCode varchar2(50),
    arrName varchar2(200)
);

create table airplaneSchedule(
    flightName VARCHAR2(20),
    depDay date,
    arrDay date,
    depTime timestamp,        
    arrTime timestamp, 
    flightTime interval day to second,       
    fullDeparture VARCHAR2(200),
    depCode varchar2(50),
    depName varchar2(200),
    fullArrival varchar2(200),
    arrCode varchar2(50),
    arrName varchar2(200),
    depRegionCode number(4),        
    arrRegionCode number(4),        
    primary key(flightName, depDay)       
);
alter table airplaneSchedule add constraint fk_depRegionCode foreign key (depRegionCode) references regionCode(code);
alter table airplaneSchedule add constraint fk_arrRegionCode foreign key (arrRegionCode) references regionCode(code);
-------------------------------------------------------------------------------------------------------------
--테이블 내용 복제
INSERT INTO airplaneSchedule(flightName, depDay, depTime,fullDeparture, depCode, depName, fullArrival, arrCode, arrName) 
    SELECT flightName, depDay, depTime, fullDeparture, depCode, depName, fullArrival, arrCode, arrName FROM airplaneDepart;

--중복된 데이터가 있는지 확인(기본키 활용)
select (flightName||depDay) from airplaneDepart group by (flightName||depDay) having count((flightName||depDay))>=2;

--arrTime 업데이트    
MERGE INTO airplaneSchedule a
USING airplaneArrive b
   ON (a.depday = b.arrday 
        and a.flightname = b.flightname)
 WHEN MATCHED THEN
      UPDATE 
         SET a.arrTime = b.arrTime;
         
--arrDay 업데이트
MERGE INTO airplaneSchedule a
USING airplaneArrive b
   ON (a.depday = b.arrday 
        and a.flightname = b.flightname)
 WHEN MATCHED THEN
      UPDATE 
         SET a.arrDay = b.arrDay;
    
--flightTime 계산하여 업데이트
update airplaneSchedule 
    set flightTime = (arrTime-depTime);

select count(*) from airplaneDepart;
select count(*) from airplaneArrive;
select count(*) from airplaneSchedule;		--13373개

--결항편 제거
select count(*) from airplaneSchedule where flightTime is null;
delete from airplaneSchedule where flightTime is null;
commit;

--distinct한 출발/도착지, 항공편 검색
select distinct(depName) from airplaneSchedule order by depName;	--48개
select distinct(arrName) from airplaneSchedule order by arrName;		--47개
select distinct(depName||arrName) from airplaneSchedule order by 1;	--98개

--다음날 도착하는 항공편 도착일 및 업데이트. 
select count(*) from airplaneSchedule where flightTime<'-00 00:00:01.000000';
update airplaneSchedule set arrday = arrday+1 where flightTime<'-00 00:00:01.000000';
update airplaneSchedule set arrtime=arrtime+1 where flightTime<'-00 00:00:01.000000';
commit;

--3/1도착편, 카이로 항공편 제거
select * from airplaneSchedule where arrday = '23/03/01';
delete from airplaneSchedule where arrday = '23/03/01';
delete from airplaneSchedule where arrname = '카이로';

--이름 변경
select count(*) from airplaneSchedule where depname like '%울란바토르%';
update airplaneSchedule set depname = '자카르타' where depname like '%수카르노%';
update airplaneSchedule set arrname = '자카르타' where arrname like '%수카르노%';
update airplaneSchedule set depname = '울란바타르' where depname like '%울란바토르%';
update airplaneSchedule set arrname = '울란바타르' where arrname like '%울란바토르%';
update airplaneSchedule set depname = '난징' where depname like '남경';
update airplaneSchedule set arrname = '난징' where arrname like '남경';
update airplaneSchedule set depname = '청두' where depname like '성도';
update airplaneSchedule set arrname = '청두' where arrname like '성도';
update airplaneSchedule set depname = '선전' where depname like '심천';
update airplaneSchedule set arrname = '선전' where arrname like '심천';
update airplaneSchedule set depname = '옌지' where depname like '연길';
update airplaneSchedule set arrname = '옌지' where arrname like '연길';
update airplaneSchedule set depname = '창춘' where depname like '장춘';
update airplaneSchedule set arrname = '창춘' where arrname like '장춘';
update airplaneSchedule set depname = '타이베이' where depname like '%타이완%';
update airplaneSchedule set arrname = '타이베이' where arrname like '%타이완%';
commit;

--지역코드 입력
select count(*) from airplaneSchedule where depname in ('광주', '김포', '대구', '여수', '인천', '제주', '청주' );
select count(*) from airplaneSchedule where arrname in ('광주', '김포', '대구', '여수', '인천', '제주', '청주' );
update airplaneSchedule set depregioncode=1 where depname in ('광주', '김포', '대구', '여수', '인천', '제주', '청주');
update airplaneSchedule set arrregioncode=1 where arrname in ('광주', '김포', '대구', '여수', '인천', '제주', '청주');
update airplaneSchedule set depregioncode=2 where depname in ('간사이', '나고야', '난징', '도쿄/나리타', '미야자키', '베이징', '삿포로', '청두', '선전', '옌지', '오키나와', '창춘', '타이베이', '푸동', '하네다', '하얼빈', '항저우', '홍콩', '후쿠오카');
update airplaneSchedule set arrregioncode=2 where arrname in ('간사이', '나고야', '난징', '도쿄/나리타', '미야자키', '베이징', '삿포로', '청두', '선전', '옌지', '오키나와', '창춘', '타이베이', '푸동', '하네다', '하얼빈', '항저우', '홍콩', '후쿠오카');
update airplaneSchedule set depregioncode=3 where depname in ('다낭', '델리', '마닐라', '방콕', '자카르타', '싱가포르', '푸껫', '프놈펜', '하노이', '호찌민');
update airplaneSchedule set arrregioncode=3 where arrname in ('다낭', '델리', '마닐라', '방콕', '자카르타', '싱가포르', '푸껫', '프놈펜', '하노이', '호찌민');
update airplaneSchedule set depregioncode=4 where depname in ('울란바타르','알마티','타슈켄트');
update airplaneSchedule set arrregioncode=4 where arrname in ('울란바타르','알마티','타슈켄트');
update airplaneSchedule set depregioncode=5 where depname in ('런던히드로', '로마', '바르셀로나', '이스탄불', '파리', '프랑크푸르트');
update airplaneSchedule set arrregioncode=5 where arrname in ('런던히드로', '로마', '바르셀로나', '이스탄불', '파리', '프랑크푸르트');
update airplaneSchedule set depregioncode=6 where depname in ('뉴욕', '로스앤젤레스', '샌프란시스코', '시애틀' ,'호놀룰루');
update airplaneSchedule set arrregioncode=6 where arrname in ('뉴욕', '로스앤젤레스', '샌프란시스코', '시애틀' ,'호놀룰루');
update airplaneSchedule set depregioncode=7 where depname in ('사이판', '시드니');
update airplaneSchedule set arrregioncode=7 where arrname in ('사이판', '시드니');

select * from airplaneSchedule where depregioncode is null;
select * from airplaneSchedule where arrregioncode is null;

commit;
-------------------------------------------------------------------------------------------------------------------
create table regionCode(
    region varchar2(100),
    code number primary key
);
insert into regionCode values('한국', 1);
insert into regionCode values('동북아시아', 2);
insert into regionCode values('동남아시아/서남아시아', 3);
insert into regionCode values('몽골/중앙아시아', 4);
insert into regionCode values('유럽', 5);
insert into regionCode values('미주(미국,캐나다,중남미)', 6);
insert into regionCode values('대양주/사이판/필리핀', 7);
insert into regionCode values('중동/아프리카', 8);

commit;