import Header from "../../components/repeat_etc/Header";
import React, {useEffect, useState} from "react";
import BgImg from "../../images/blue-galaxy-wallpaper.jpg";
import Check from "../../images/unchecked.png";
import "../../css/study_css/TeamBlog.css";
import axios from "axios";
import {Link, useNavigate, useParams} from "react-router-dom";
import TeamBlogcss from "../../css/study_css/TeamBlog.css";
import {useLocation} from "react-router-dom";
import TeamToDoList from "../TeamToDo/TeamToDoList";
import MapNaverDefault from "../../components/map/MapNaverDefault";
import checkbox from "../../images/check.png";
import uncheckbox from "../../images/unchecked.png";
import Category from "../../components/repeat_etc/Category";
import Chat from "../../components/chat/Chat";
import TeamSchedule from "../../components/teamschedules/TeamSchedule";


const TeamBlog = () => {
    const accessToken = localStorage.getItem('accessToken');
    const study = useLocation();
    const navigate = useNavigate();
    const {studyId} = study.state;
    const [parsedTodos, setParsedTodos] = useState([]);
    const [today, setToday] = useState(new Date());
    const Year = today.getFullYear();
    const Month = today.getMonth() + 1;
    const Dates = today.getDate()
    const studyIdAsNumber = parseFloat(studyId);
    console.log("받아온 Study", study);

    if (studyId !== undefined) {
        console.log("Study ID:", studyId);
    } else {
        console.log("Study ID is undefined.");
    }

    const id = parseFloat(studyId);
    const [Member, setMember] = useState([]);
    const [studyItem, setStudyItem] = useState([]);
    const getTodoItemClassName = (checked) => {
        return checked ? "checked" : "unchecked";
    };
    const ShowAllToDo = () => {
        navigate(`/${studyIdAsNumber}/teamblog/TeamToDoList`,
            {
                state: {
                    studyId: studyId,
                    Member: Member,
                    selectStudy: studyItem,
                }
            })
    }

    //TODO 참여멤버 리스트 가지고오기
    useEffect(() => {
        axios.get(`http://localhost:8080/api/v2/studies/${id}/study-member`, {
            withCredentials: true,
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        })
            .then((res) => {
                console.log("참여멤버 get 성공 : ", res.data);

                const studymemberList = res.data;

                setMember(studymemberList.data);

            })
            .catch((error) => {
                console.error("참여멤버 get 실패:", error);
            });

        // 스터디 id로 스터디 객체 가져오기
        axios.get(`http://localhost:8080/api/v2/studies/${id}`, {
            withCredentials: true,
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        }).then((res) => {
            setStudyItem(res.data);
            console.error("스터디 세부 데이터 가져오기 성공:", res.data);
        })
            .catch((error) => {
                console.error("스터디 세부 데이터 가져오기 실패:", error);
            });

    }, [accessToken]);
    //해당 스터디의 투두 가져오기
    useEffect(() => {
        axios.get(`http://localhost:8080/todo/${studyIdAsNumber}`, {
            params: {
                year: Year, month: Month,
            }, headers: {
                Authorization: `Bearer ${accessToken}`,
            },
        }).then((response) => {
            console.log('스터디별 투두리스트 가져오기 성공:', response.data);
            setParsedTodos((prevTodos) => (response.data))

        }).catch((error) => {
            console.log('스터디별 투두리스트 가져오기 실패:', error);
        })
    }, [studyIdAsNumber]);

    //마이페이지 투두 연동
    const [filteredToDo, setFilteredToDo] = useState([]);
    useEffect(() => {
        if (Array.isArray(parsedTodos)) {
            const filteredToDo = parsedTodos.filter((todo) => {
                const todoDueDate = new Date(todo.dueDate).toDateString();
                const todayDate = today.toDateString();
                return todoDueDate === todayDate;
            });
            console.log("filteredToDo: ", filteredToDo);
            setFilteredToDo(filteredToDo);
        } else {
            console.error("parsedTodos is not an array.");
        }
    }, [parsedTodos]);

    return (
        <div>
            <Header showSideCenter={true}/>
            <div className={"main_content"}>
                <Category/>
                <div className="team_blog">
                    <h1 className="page_title">STUDY TEAM BLOG</h1>
                    <div className="img_wrapper">
                        {/*<img src={BgImg}/>*/}
                        <div className="team_info">
                            <h2 className="study_title">{studyItem.title}</h2>
                            <h3 className="study_duration">{studyItem.activityStart} ~ {studyItem.activityDeadline}</h3>
                        </div>
                    </div>
                    <div className="gnb_bg">
                        <ul className="gnb">
                            <li>팀블로그 홈</li>
                            <li>TODO</li>
                            <li>일정</li>
                            <li>팀 커뮤니티</li>
                        </ul>
                    </div>
                    <div className="content">
                        <div className={"content-left"}>
                            <div className={"todo_content"}>
                                <div className="todos">
                                    <div className="tag">
                                        <p>오늘의 할 일</p>
                                        <button id="more" onClick={ShowAllToDo}>전체보기</button>
                                    </div>
                                    <div id="detail">
                                        <span id="today">{`${Year}. ${Month}. ${Dates}`}</span>
                                        <hr/>
                                        {filteredToDo ? (
                                            <ul id="todocontent">
                                                {filteredToDo.map((todo) => (
                                                    <li
                                                        key={todo.id}
                                                        // className={getTodoItemClassName(todo.toDoStatus)}
                                                    >
                                                        {/*{todo.toDoStatus ? (*/}
                                                        {/*    <img src={checkbox} alt="checked" width="20px"/>*/}
                                                        {/*) : (*/}
                                                        {/*    <img src={uncheckbox} alt="unchecked" width="20px"/>*/}
                                                        {/*)}*/}
                                                        <div id="todotext">{todo.study.title} |</div>
                                                        <div id="todotext">{todo.task}</div>
                                                    </li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <div className="empty_today_todo">
                                          <span>
                                            할 일이 없습니다.<br/> 할 일을 입력해주세요.
                                          </span>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                            <div className={"location_content"}>
                                <div className="location">
                                    <div className="tag">
                                        <p>중간장소 찾기</p>
                                    </div>
                                    <div id="detail">
                                        <MapNaverDefault studyId={studyId} Member={Member}/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className={"content_right"}>
                            <div className={"chat_content"}>
                            <Chat studyId ={studyId}/>
                            </div>
                            <div className={"schedule_content"}>
                                <TeamSchedule studyIdAsNumber={studyIdAsNumber}/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};
export default TeamBlog;