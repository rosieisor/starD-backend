// import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
// import {faArrowLeft} from "@fortawesome/free-solid-svg-icons";
// import React, {useCallback, useState} from 'react';
// import { useParams, useNavigate, Link } from "react-router-dom";
//
// import "../css/StudyOpenForm.css";
// import RealEstate from "../components/RealEstate";
//
// const StudyOpenForm = ({sideheader}) => {
//     const navigate = useNavigate();
//
//     const [showSelect, setShowSelect] = useState(false);
//     const [selectedOption, setSelectedOption] = useState(null);
//
//     const [formData, setFormData] = useState({
//         title: "",
//         tag: "",
//         author:"",
//         number: "",
//         onoff: "",
//         deadline: "",
//         duration: "",
//         description: "",
//         created_date: new Date(),
//     });
//
//     const handleInputChange = (e) => {
//         setFormData({
//             ...formData,
//             [e.target.name]: e.target.value,
//         })
//     };
//
//     const handleRadioChange = (e) => {
//         const selectedValue = e.target.value;
//         setSelectedOption(selectedValue);
//         setFormData({
//             ...formData,
//             onoff: e.target.value,
//         })
//         setShowSelect(selectedValue === "offline");
//     }
//     const handleSubmit = useCallback(e => {
//         if (formData != null){
//             onInsert(formData);
//         } else {
//             alert("폼의 내용을 채워주세요");
//             return;
//         }
//         setFormData()
//         e.preventDefault();
//         // 여기서 formData를 사용하여 데이터 처리하거나 API 호출 등을 수행합니다.
//         // 예를 들어 navigate("/other-page", { state: formData })와 같이 사용할 수 있습니다.
//         console.log('Form data submitted:', formData);
//         navigate("/myopenstudy", {state: formData});
//     })
//     const studyopenform = () => {
//         return (
//             <form className="study_open_form" onSubmit={handleSubmit}>
//                 <div>
//                     <div className="left">
//                         <div>
//                             <span>제목</span>
//                             <input type="text" name="title" value={formData.title} onChange={handleInputChange} className="inputbox" placeholder="제목을 입력해주세요"/>
//                         </div>
//                         <div>
//                             <span>모집 인원</span>
//                             <input type="text" name="number" value={formData.number} onChange={handleInputChange} className="inputbox" placeholder="모집 인원을 입력해주세요"/>
//                         </div>
//                         <div>
//                             <span>모집 마감일</span>
//                             <input type="date" name="deadline" value={formData.deadline} onChange={handleInputChange} className="inputbox" placeholder="스터디 모집 마감일을 선택해주세요"/>
//                         </div>
//                     </div>
//                     <div className="right">
//                         <div>
//                             <span>분야</span>
//                             <input type="text" name="tag" value={formData.tag} onChange={handleInputChange} className="inputbox" placeholder="사용할 태그를 입력해주세요"/>
//                         </div>
//                         <div>
//                             <span className="onoff_title">진행 방식</span>
//                             <div className="onoff">
//                                 <input type="radio" value="online" name="onoff" onChange={handleRadioChange}/>온라인
//                                 <input type="radio" value="offline" name="onoff" onChange={handleRadioChange} />오프라인
//                                     {showSelect && (
//                                         <RealEstate />
//                                     )}
//                             </div>
//                         </div>
//                         <div>
//                             <span>스터디 기간</span>
//                             <input type="date" name="duration" value={formData.duration} onChange={handleInputChange} className="inputbox" placeholder="스터디 진행 기간을 선택해주세요"/>
//                         </div>
//
//                     </div>
//                 </div>
//                 <div className="study_open_detail">
//                     <span>상세 내용</span>
//                     <textarea placeholder="상세 내용을 입력해주세요" name="description" onChange={handleInputChange} defaultValue={formData.description}/>
//                 </div>
//                 <div className="btn">
//                     <input type="submit" value="모집하기" className="recruit_btn"/>
//                 </div>
//             </form>
//         )
//     }
//     return (<div>
//         {sideheader}
//         <div className="study_detail_container">
//             <h1>STAR TOUR STORY</h1>
//             <div className="arrow_left">
//                 <FontAwesomeIcon
//                     icon={faArrowLeft}
//                     onClick={() => navigate(-1)}
//                 />
//             </div>
//             {studyopenform()}
//         </div>
//     </div>);
// }
// export default StudyOpenForm;